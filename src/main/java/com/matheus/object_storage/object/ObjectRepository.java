package com.matheus.object_storage.object;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectRepository {
    BucketObject save(BucketObject bucketObject);
    Optional<BucketObject> findById(UUID id);
    List<BucketObject> findByBucketId(UUID id);
    Optional<BucketObject> findByBucketIdAndKey(UUID id, String key);
    void deleteById(UUID id);
}
