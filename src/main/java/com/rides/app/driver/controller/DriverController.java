package com.rides.app.driver.controller;

import com.rides.app.driver.dto.DriverDto;
import com.rides.app.driver.entity.Driver;
import com.rides.app.driver.entity.Vehicle;
import com.rides.app.driver.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<Driver> create(@RequestBody DriverDto dto) {
        Driver created = driverService.createDriver(dto);
        return ResponseEntity.created(URI.create("/api/v1/drivers/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> get(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriver(id));
    }

    @GetMapping
    public ResponseEntity<List<Driver>> list() {
        return ResponseEntity.ok(driverService.listDrivers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> update(@PathVariable Long id, @RequestBody DriverDto dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Driver> setStatus(@PathVariable Long id, @RequestParam("active") boolean active) {
        return ResponseEntity.ok(driverService.setStatus(id, active));
    }

    @PostMapping("/{id}/vehicles")
    public ResponseEntity<Void> addVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        driverService.addOrUpdateVehicle(id, vehicle);
        return ResponseEntity.created(URI.create("/api/v1/drivers/" + id + "/vehicles")).build();
    }

    @GetMapping("/{id}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehicles(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getVehicles(id));
    }
}
