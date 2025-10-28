package com.rides.app.driver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "driver_location", indexes = {
        @Index(name = "idx_driver_location_driver", columnList = "driver_id"),
        @Index(name = "idx_driver_location_lat", columnList = "lat"),
        @Index(name = "idx_driver_location_lon", columnList = "lon")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLocation {
    @Id
    @Column(name = "driver_id")
    private Long driverId; // PK = driver id

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    private Double speed;   // optional
    private Double heading; // optional
}
