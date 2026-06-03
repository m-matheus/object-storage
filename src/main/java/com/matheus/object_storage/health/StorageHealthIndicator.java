package com.matheus.object_storage.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import com.matheus.object_storage.bucket.BucketRepository;

@Component
public class StorageHealthIndicator implements HealthIndicator {
    
    private final BucketRepository bucketRepository;

    public StorageHealthIndicator(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    @Override
    public Health health() {
        try {
            bucketRepository.findAll();

            return Health.up()
                .withDetail("storage", "in-memory")
                .withDetail("status", "operational")
                .build();

        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
