package com.rides.app.driver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles",
        uniqueConstraints = @UniqueConstraint(columnNames = "plate"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String plate;

    private Integer year;

    @Column(name = "vehicle_type")
    private String vehicleType;

    // Relationship to Driver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
}
