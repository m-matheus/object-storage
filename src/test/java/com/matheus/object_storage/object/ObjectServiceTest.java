package com.matheus.object_storage.object;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.matheus.object_storage.bucket.Bucket;
import com.matheus.object_storage.bucket.BucketNotFoundException;
import com.matheus.object_storage.bucket.BucketRepository;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ObjectServiceTest {
    @Mock
    ObjectRepository objectRepository;

    @Mock
    BucketRepository bucketRepository;

    @InjectMocks
    ObjectService service;

    private final UUID bucketId = UUID.randomUUID();
    private final UUID objectId = UUID.randomUUID();
    private final Bucket bucket = new Bucket(bucketId, "test", "anonymous", Instant.now());
    private final BucketObject bucketObject = new BucketObject(objectId, "test/file.txt", bucketId,
            "text/plain", 5L, Instant.now(), "hello".getBytes());

    @Test
    void uploadObject_whenBucketExists_shouldReturnSavedObject() {
        given(bucketRepository.findById(bucketId)).willReturn(Optional.of(bucket));
        given(objectRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        BucketObject result =
                service.uploadObject(bucketId, "test/file.txt", "text/plain", "hello".getBytes());

        assertThat(result.key()).isEqualTo("test/file.txt");
        assertThat(result.bucketId()).isEqualTo(bucketId);
        verify(objectRepository).save(any(BucketObject.class));
    }

    @Test
    void uploadObject_whenBucketNotFound_shouldThrowBucketNotFoundException() {
        given(bucketRepository.findById(bucketId)).willReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> service.uploadObject(bucketId,
                "test/file.txt", "text/plain", "hello".getBytes()));

        verify(objectRepository, never()).save(any());
    }

    @Test
    void getObject_whenObjectExists_shouldReturnObject() {
        given(objectRepository.findById(objectId)).willReturn(Optional.of(bucketObject));

        BucketObject result = service.getObject(bucketId, objectId);

        assertThat(result.id()).isEqualTo(objectId);
    }

    @Test
    void getObject_whenObjectNotFound_shouldThrowObjectNotFoundException() {
        given(objectRepository.findById(objectId)).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getObject(bucketId, objectId));
    }

    @Test
    void getObject_whenObjectBelongsToDifferentBucket_shouldThrowObjectNotFoundException() {
        UUID otherBucketId = UUID.randomUUID();
        given(objectRepository.findById(objectId)).willReturn(Optional.of(bucketObject));

        assertThrows(ObjectNotFoundException.class,
                () -> service.getObject(otherBucketId, objectId));
    }

    @Test
    void listObjects_whenBucketExists_shouldReturnObjects() {
        given(bucketRepository.findById(bucketId)).willReturn(Optional.of(bucket));
        given(objectRepository.findByBucketId(bucketId)).willReturn(List.of(bucketObject));

        List<BucketObject> result = service.listObjects(bucketId);

        assertThat(result).hasSize(1);
    }

    @Test
    void listObjects_whenBucketNotFound_shouldThrowBucketNotFoundException() {
        given(bucketRepository.findById(bucketId)).willReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> service.listObjects(bucketId));
    }

    @Test
    void deleteObject_whenObjectExists_shouldCallDeleteById() {
        given(objectRepository.findById(objectId)).willReturn(Optional.of(bucketObject));

        service.deleteObject(bucketId, objectId);

        verify(objectRepository).deleteById(objectId);
    }

    @Test
    void deleteObject_whenObjectNotFound_shouldThrowObjectNotFoundException() {
        given(objectRepository.findById(objectId)).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.deleteObject(bucketId, objectId));

        verify(objectRepository, never()).deleteById(any());
    }

}
