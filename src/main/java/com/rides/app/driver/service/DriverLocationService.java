package com.rides.app.driver.service;

import com.rides.app.driver.entity.Driver;
import com.rides.app.driver.entity.DriverLocation;
import com.rides.app.driver.events.DriverEvents;
import com.rides.app.driver.exception.TooManyRequestsException;
import com.rides.app.driver.kafka.KafkaProducerService;
import com.rides.app.driver.repository.DriverLocationRepository;
import com.rides.app.driver.repository.DriverRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DriverLocationService {

    private final DriverLocationRepository locationRepository;
    private final DriverRepository driverRepository;
    private final KafkaProducerService producer;

    private final ConcurrentHashMap<Long, Long> lastUpdateMap = new ConcurrentHashMap<>();

    @Value("${app.location.min-interval-ms:1000}")
    private long minUpdateIntervalMillis;

    @Value("${app.kafka.topic.driver-location:driver.location.updated}")
    private String topicDriverLocation;

    public DriverLocationService(DriverLocationRepository locationRepository,
                                 DriverRepository driverRepository,
                                 KafkaProducerService producer) {
        this.locationRepository = locationRepository;
        this.driverRepository = driverRepository;
        this.producer = producer;
    }

    @Transactional
    public DriverLocation updateLocation(Long driverId, double lat, double lon, Double speed, Double heading) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));

        // Simple rate limiting
        long now = Instant.now().toEpochMilli();
        Long last = lastUpdateMap.get(driverId);
        if (last != null && (now - last) < minUpdateIntervalMillis) {
            throw new TooManyRequestsException("Location updates are limited to one per " + minUpdateIntervalMillis + " ms.");
        }
        lastUpdateMap.put(driverId, now);

        DriverLocation loc = locationRepository.findById(driverId)
                .orElse(DriverLocation.builder().driverId(driverId).build());

        loc.setLat(lat);
        loc.setLon(lon);
        loc.setSpeed(speed);
        loc.setHeading(heading);
        loc.setLastSeenAt(Instant.now());

        DriverLocation saved = locationRepository.save(loc);

        // --- Publish driver.location.updated event ---
        producer.send(topicDriverLocation,
                DriverEvents.DriverLocationUpdated.builder()
                        .driverId(driverId)
                        .lat(lat)
                        .lon(lon)
                        .ts(Instant.now())
                        .build());

        return saved;
    }

    public List<NearbyDriver> findNearby(double lat, double lon, double radiusKm, int limit) {
        if (radiusKm <= 0) radiusKm = 5.0;
        if (limit <= 0) limit = 20;

        List<Object[]> rows = locationRepository.findNearbyActiveRaw(lat, lon, radiusKm, limit);
        return rows.stream()
                .map(r -> new NearbyDriver(
                        ((Number) r[0]).longValue(),
                        ((Number) r[1]).doubleValue(),
                        ((Number) r[2]).doubleValue(),
                        r[3] == null ? null : r[3].toString()
                ))
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NearbyDriver {
        private Long driverId;
        private Double lat;
        private Double lon;
        private String lastSeenAt;
    }
}
