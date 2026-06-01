package com.matheus.object_storage.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryObjectRepository implements ObjectRepository {
    private final Map<UUID, BucketObject> store = new HashMap<>();

    @Override
    public BucketObject save(BucketObject bucketObject) {
        store.put(bucketObject.id(), bucketObject);
        return bucketObject;
    }

    @Override
    public Optional<BucketObject> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<BucketObject> findByBucketId(UUID id) {
        return store.values().stream().filter(o -> o.bucketId().equals(id)).toList();
    }

    @Override
    public Optional<BucketObject> findByBucketIdAndKey(UUID id, String key) {
        return store.values().stream().filter(o -> o.bucketId().equals(id)).filter(o -> o.key().equals(key)).findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

}
