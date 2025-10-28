package com.rides.app.driver.repository;

import com.rides.app.driver.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {

    /**
     * Find nearby active drivers (Haversine), only drivers where drivers.is_active = true.
     * Returns rows: driver_id, lat, lon, last_seen_at
     */
    @Query(value = """
        SELECT dl.driver_id, dl.lat, dl.lon, dl.last_seen_at
        FROM driver_location dl
        JOIN drivers d ON dl.driver_id = d.id
        WHERE d.is_active = true
          AND (6371 * 2 * ASIN(
               SQRT(
                 POWER(SIN(RADIANS(dl.lat - :lat) / 2), 2) +
                 COS(RADIANS(:lat)) * COS(RADIANS(dl.lat)) *
                 POWER(SIN(RADIANS(dl.lon - :lon) / 2), 2)
               )
          )) <= :radiusKm
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNearbyActiveRaw(double lat, double lon, double radiusKm, int limit);
}
