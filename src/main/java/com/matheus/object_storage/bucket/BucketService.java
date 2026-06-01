package com.matheus.object_storage.bucket;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BucketService {
    private final BucketRepository repository;

    public BucketService(BucketRepository repository) {
        this.repository = repository;
    }

    public Bucket createBucket(String name) {
        if (repository.existsByName(name)) {
            throw new BucketAlreadyExistsException("Bucket already exists: " + name);
        }

        Bucket bucket = new Bucket(UUID.randomUUID(), name, "anonymous", Instant.now());
        return repository.save(bucket);
    }

    public Bucket getBucket(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BucketNotFoundException("Bucket not found: " + id));
    }

    public List<Bucket> listBuckets() {
        return repository.findAll();
    }

    public void deleteBucket(UUID id) {
        repository.findById(id)
                .orElseThrow(() -> new BucketNotFoundException("Bucket not found: " + id));

        repository.deleteById(id);
    }
}
