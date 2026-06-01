package com.matheus.object_storage.bucket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BucketRepository {
    Bucket save(Bucket bucket);
    Optional<Bucket> findById(UUID id);
    Optional<Bucket> findByName(String name);
    List<Bucket> findAll();
    void deleteById(UUID id);
    boolean existsByName(String name);
}
