package com.rides.app.driver.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.math.BigDecimal;

public class DriverEvents {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DriverRegistered {
        private Long driverId;
        private String name;
        private String phone;
        private String email;
        private String licenseNumber;
        private Instant createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DriverStatusChanged {
        private Long driverId;
        private boolean isActive;
        private Instant changedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DriverLocationUpdated {
        private Long driverId;
        private double lat;
        private double lon;
        private Instant ts;
    }
}

