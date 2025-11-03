package com.rides.app.driver.util;

import com.opencsv.CSVReader;
import com.rides.app.driver.entity.Driver;
import com.rides.app.driver.entity.Vehicle;
import com.rides.app.driver.repository.DriverRepository;
import com.rides.app.driver.repository.VehicleRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Robust CSV loader: header-aware, idempotent for drivers & vehicles.
 * Skips or updates existing vehicle by plate to avoid unique constraint violations.
 */
@Component
@Profile("!prod")
@Slf4j
public class DataLoader {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final String csvPath;

    public DataLoader(
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            @Value("${app.data.csv-path:./rhfd_drivers.csv}") String csvPath) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.csvPath = csvPath;
    }

    @PostConstruct
    @Transactional
    public void loadData() {
        try {
            Path path = Path.of(csvPath);
            if (!Files.exists(path)) {
                log.warn("CSV file not found at {}, skipping data load.", csvPath);
                return;
            }

            log.info("Starting CSV data load from {}", csvPath);
            try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
                String[] header = reader.readNext();
                if (header == null) {
                    log.warn("CSV is empty: {}", csvPath);
                    return;
                }
                Map<String,Integer> idx = buildIndex(header);

                String[] line;
                int imported = 0;
                int skipped = 0;
                while ((line = reader.readNext()) != null) {
                    try {
                        String name = get(line, idx, "name");
                        String phone = get(line, idx, "phone");
                        String email = get(line, idx, "email");
                        String license = firstNonEmpty(get(line, idx, "license"), get(line, idx, "licenseNumber"), get(line, idx, "license_number"));
                        String plate = firstNonEmpty(get(line, idx, "plate"), get(line, idx, "vehicle_plate"));
                        String make = firstNonEmpty(get(line, idx, "make"), get(line, idx, "vehicle_make"));
                        String model = firstNonEmpty(get(line, idx, "model"), get(line, idx, "vehicle_model"));
                        String yearStr = firstNonEmpty(get(line, idx, "year"), get(line, idx, "vehicle_year"));

                        if (phone == null || phone.isBlank()) {
                            log.warn("Skipping CSV line - missing phone");
                            skipped++;
                            continue;
                        }

                        // create or fetch driver by phone
                        Driver driver = driverRepository.findByPhone(phone).orElseGet(() -> {
                            Driver d = Driver.builder()
                                    .name(name == null || name.isBlank() ? "Driver-" + phone : name)
                                    .phone(phone)
                                    .email(email)
                                    .licenseNumber(license)
                                    .isActive(true)
                                    .build();
                            return driverRepository.save(d);
                        });

                        // handle vehicle if plate present
                        if (plate != null && !plate.isBlank()) {
                            Optional<Vehicle> existingVehicleOpt = vehicleRepository.findByPlate(plate);
                            if (existingVehicleOpt.isPresent()) {
                                Vehicle existing = existingVehicleOpt.get();
                                // If plate exists but linked to different driver, log and skip/update based on your rule
                                if (!existing.getId().equals(driver.getId())) {
                                    log.warn("Plate {} already exists for driver {}. Skipping assign to driver {}",
                                            plate, existing.getId(), driver.getId());
                                    skipped++;
                                } else {
                                    // same driver — update fields if any changed
                                    boolean changed = false;
                                    if (make != null && !make.isBlank() && !make.equals(existing.getMake())) {
                                        existing.setMake(make);
                                        changed = true;
                                    }
                                    if (model != null && !model.isBlank() && !model.equals(existing.getModel())) {
                                        existing.setModel(model);
                                        changed = true;
                                    }
                                    if (yearStr != null && !yearStr.isBlank()) {
                                        try {
                                            Integer y = Integer.valueOf(yearStr);
                                            if (!y.equals(existing.getYear())) {
                                                existing.setYear(y);
                                                changed = true;
                                            }
                                        } catch (NumberFormatException nfe) {
                                            // ignore parse errors
                                        }
                                    }
                                    if (changed) {
                                        vehicleRepository.save(existing);
                                    } else {
                                        // nothing to do
                                    }
                                    imported++;
                                }
                            } else {
                                // create new vehicle
                                try {
                                    Vehicle v = Vehicle.builder()
                                            .id(driver.getId())
                                            .plate(plate)
                                            .make(make)
                                            .model(model)
                                            .year((yearStr == null || yearStr.isBlank()) ? null : Integer.valueOf(yearStr))
                                            .build();
                                    vehicleRepository.save(v);
                                    imported++;
                                } catch (Exception e) {
                                    // catch other unexpected DB exceptions to avoid stopping startup
                                    log.warn("Failed to save vehicle plate {}: {}", plate, e.getMessage());
                                    skipped++;
                                }
                            }
                        } else {
                            // no vehicle data — count as imported driver only
                            imported++;
                        }
                    } catch (Exception e) {
                        log.error("Error processing CSV line: {}", e.getMessage(), e);
                        skipped++;
                    }
                }

                log.info("CSV data load finished: imported={}, skipped={}", imported, skipped);
            }
        } catch (Exception e) {
            log.error("DataLoader failed: {}", e.getMessage(), e);
        }
    }

    private Map<String,Integer> buildIndex(String[] header) {
        Map<String,Integer> idx = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            idx.put(header[i].trim().toLowerCase(), i);
        }
        return idx;
    }

    private String get(String[] arr, Map<String,Integer> idx, String key) {
        Integer i = idx.get(key.toLowerCase());
        if (i == null) return null;
        if (i >= arr.length) return null;
        return arr[i] == null ? null : arr[i].trim();
    }

    private String firstNonEmpty(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
}
