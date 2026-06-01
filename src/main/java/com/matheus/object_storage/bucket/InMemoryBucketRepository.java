package com.matheus.object_storage.bucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBucketRepository implements BucketRepository {
    private final Map<UUID, Bucket> store = new HashMap<>();

    @Override
    public Bucket save(Bucket bucket) {
        store.put(bucket.id(), bucket);
        return bucket;
    }

    @Override
    public Optional<Bucket> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Bucket> findByName(String name) {
        return store.values().stream().filter(b -> b.name().equals(name)).findFirst();
    }

    @Override
    public List<Bucket> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
    
}
