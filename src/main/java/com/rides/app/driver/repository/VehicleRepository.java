package com.rides.app.driver.repository;

import com.rides.app.driver.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByDriverId(Long driverId);
    Optional<Vehicle> findByPlate(String plate); // <- add this
    boolean existsByPlate(String plate);
}
