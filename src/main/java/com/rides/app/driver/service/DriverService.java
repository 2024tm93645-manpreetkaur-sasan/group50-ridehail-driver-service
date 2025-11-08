package com.rides.app.driver.service;

import com.rides.app.driver.dto.DriverDto;
import com.rides.app.driver.entity.Driver;
import com.rides.app.driver.entity.Vehicle;
import com.rides.app.driver.repository.DriverRepository;
import com.rides.app.driver.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Value("${app.kafka.topic.driver-registered:driver.registered}")
    private String topicDriverRegistered;

    @Value("${app.kafka.topic.driver-status:driver.status.changed}")
    private String topicDriverStatus;

    // ======== CRUD ========

    public List<Driver> listDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriver(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + id));
    }

    public Driver createDriver(DriverDto dto) {
        Driver driver = Driver.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .licenseNumber(dto.getLicenseNumber())
                .isActive(true)
                .build();

        Driver saved = driverRepository.save(driver);

        return saved;
    }

    public Driver updateDriver(Long id, DriverDto dto) {
        Driver existing = getDriver(id);
        existing.setName(dto.getName());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setLicenseNumber(dto.getLicenseNumber());
        return driverRepository.save(existing);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    // ======== STATUS ========

    public Driver setStatus(Long id, boolean active) {
        Driver driver = getDriver(id);
        driver.setIsActive(active);
        Driver saved = driverRepository.save(driver);

        return saved;
    }

    // ======== VEHICLES ========

    public void addOrUpdateVehicle(Long driverId, Vehicle vehicle) {
        Driver driver = getDriver(driverId);
        vehicle.setDriver(driver);
        vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getVehicles(Long driverId) {
        return vehicleRepository.findByDriverId(driverId);
    }
}
