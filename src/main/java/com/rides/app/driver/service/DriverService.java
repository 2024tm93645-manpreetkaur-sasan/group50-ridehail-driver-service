package com.rides.app.driver.service;

import com.rides.app.driver.dto.DriverDto;
import com.rides.app.driver.entity.Driver;
import com.rides.app.driver.entity.Vehicle;
import com.rides.app.driver.exception.NotFoundException;
import com.rides.app.driver.repository.DriverRepository;
import com.rides.app.driver.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public DriverService(DriverRepository driverRepository, VehicleRepository vehicleRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Driver createDriver(DriverDto dto) {
        Driver d = Driver.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .licenseNumber(dto.getLicenseNumber())
                .isActive(Boolean.valueOf(dto.getIsActive() == null || dto.getIsActive()))
                .build();
        return driverRepository.save(d);
    }

    public Driver getDriver(Long id) {
        return driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Driver not found: " + id));
    }

    public List<Driver> listDrivers() {
        return driverRepository.findAll();
    }

    @Transactional
    public Driver updateDriver(Long id, DriverDto dto) {
        Driver d = getDriver(id);
        if (dto.getName() != null) d.setName(dto.getName());
        if (dto.getEmail() != null) d.setEmail(dto.getEmail());
        if (dto.getLicenseNumber() != null) d.setLicenseNumber(dto.getLicenseNumber());
        if (dto.getIsActive() != null) d.setIsActive(dto.getIsActive());
        return driverRepository.save(d);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver d = getDriver(id);
        driverRepository.delete(d);
    }

    @Transactional
    public Driver setStatus(Long id, boolean active) {
        Driver d = getDriver(id);
        d.setIsActive(Boolean.valueOf(active));
        // TODO: publish driver.status.changed event
        return driverRepository.save(d);
    }

    @Transactional
    public Driver addOrUpdateVehicle(Long driverId, Vehicle vehicle) {
        getDriver(driverId); // validate exists
        vehicle.setDriverId(driverId);
        return driverRepository.findById(driverId)
                .map(dr -> {
                    vehicleRepository.save(vehicle);
                    return dr;
                }).orElseThrow(() -> new NotFoundException("Driver not found: " + driverId));
    }

    public List<Vehicle> getVehicles(Long driverId) {
        return vehicleRepository.findByDriverId(driverId);
    }
}
