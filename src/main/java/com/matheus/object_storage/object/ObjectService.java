package com.matheus.object_storage.object;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.matheus.object_storage.bucket.BucketNotFoundException;
import com.matheus.object_storage.bucket.BucketRepository;

@Service
public class ObjectService {
    private final ObjectRepository objectRepository;
    private final BucketRepository bucketRepository;

    public ObjectService(ObjectRepository objectRepository, BucketRepository bucketRepository) {
        this.objectRepository = objectRepository;
        this.bucketRepository = bucketRepository;
    }

    public BucketObject uploadObject(UUID bucketId, String key, String contentType, byte[] data) {

        bucketRepository.findById(bucketId)
                .orElseThrow(() -> new BucketNotFoundException("Bucket not found: " + bucketId));

        BucketObject object = new BucketObject(UUID.randomUUID(), key, bucketId, contentType,
                data.length, Instant.now(), data);

        return objectRepository.save(object);
    }

    public BucketObject getObject(UUID bucketId, UUID objectId) {

        BucketObject object = objectRepository.findById(objectId).orElseThrow(() -> new ObjectNotFoundException("Object not found: " + objectId));

        if (!object.bucketId().equals(bucketId)) {
            throw new ObjectNotFoundException("Object not found: " + objectId);
        }

        return object;
    }

    public List<BucketObject> listObjects(UUID bucketId) {
        bucketRepository.findById(bucketId).orElseThrow(() -> new BucketNotFoundException("Bucket not found: " + bucketId));
        
        return objectRepository.findByBucketId(bucketId);
    }

    public void deleteObject(UUID bucketId, UUID objectId) {
        getObject(bucketId, objectId);
        objectRepository.deleteById(objectId);
    }
}
