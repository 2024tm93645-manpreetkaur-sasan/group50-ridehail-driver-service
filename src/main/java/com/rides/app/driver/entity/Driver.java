package com.rides.app.driver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "drivers", indexes = {
        @Index(name = "idx_driver_phone", columnList = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    private String email;

    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Use BigDecimal for fixed-point numeric types when you need precision/scale.
     * Here we store average rating with max 3 digits and 2 fractional digits (e.g. 9.50)
     */
    @Column(name = "rating_avg", precision = 5, scale = 2) // total 5 digits, 2 after decimal
    private BigDecimal ratingAvg = BigDecimal.valueOf(0.00);

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
