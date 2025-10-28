package com.rides.app.driver.controller;

import com.rides.app.driver.entity.DriverLocation;
import com.rides.app.driver.service.DriverLocationService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverLocationController {
    private final DriverLocationService locationService;

    public DriverLocationController(DriverLocationService locationService) {
        this.locationService = locationService;
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<DriverLocation> updateLocation(@PathVariable("id") Long id,
                                                         @RequestBody LocationRequest req) {
        DriverLocation updated = locationService.updateLocation(id, req.getLat(), req.getLon(), req.getSpeed(), req.getHeading());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<DriverLocationService.NearbyDriver>> nearbyDrivers(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam(value = "radiusKm", required = false, defaultValue = "5") double radiusKm,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit
    ) {
        List<DriverLocationService.NearbyDriver> list = locationService.findNearby(lat, lon, radiusKm, limit);
        return ResponseEntity.ok(list);
    }

    @Data
    public static class LocationRequest {
        private double lat;
        private double lon;
        private Double speed;
        private Double heading;
    }
}
