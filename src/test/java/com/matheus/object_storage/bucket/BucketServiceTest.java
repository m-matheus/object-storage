package com.matheus.object_storage.bucket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {
    @Mock
    BucketRepository repository;

    @InjectMocks
    BucketService service;

    private final UUID bucketId = UUID.randomUUID();
    private final Bucket bucket = new Bucket(bucketId, "test", "anonymous", Instant.now());

    @Test
    void createBucket_whenNameIsUnique_shouldReturnSavedBucket() {
        given(repository.existsByName("test")).willReturn(false);

        given(repository.save(any())).will(inv -> inv.getArgument(0));

        Bucket result = service.createBucket("test");

        assertThat(result.name()).isEqualTo("test");
        verify(repository).save(any(Bucket.class));
    }

    @Test
    void createBucket_whenNameAlreadyExists_shouldThrowBucketAlreadyExistsException() {
        given(repository.existsByName("test")).willReturn(true);

        assertThrows(BucketAlreadyExistsException.class, () -> service.createBucket("test"));

        verify(repository, never()).save(any());
    }

    @Test
    void getBucket_whenIdExists_shouldReturnBucket() {
        given(repository.findById(bucketId)).willReturn(Optional.of(bucket));

        Bucket result = service.getBucket(bucketId);

        assertThat(result.id()).isEqualTo(bucketId);
        assertThat(result.name()).isEqualTo("test");
    }

    @Test
    void getBucket_whenIdNotFound_shouldThrowBucketNotFoundException() {
        given(repository.findById(bucketId)).willReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> service.getBucket(bucketId));
    }

    @Test
    void listBuckets_shouldReturnAllBuckets() {
        Bucket bucket2 = new Bucket(UUID.randomUUID(), "test2", "anonymous", Instant.now());
        
        given(repository.findAll()).willReturn(List.of(bucket, bucket2));
        
        List<Bucket> result = service.listBuckets();

        assertThat(result).hasSize(2);
    }

    @Test
    void deleteBucket_whenExists_shouldCallDeleteById() {
        given(repository.findById(bucketId)).willReturn(Optional.of(bucket));

        service.deleteBucket(bucketId);

        verify(repository).deleteById(bucketId);
    }

    @Test
    void deleteBucket_whenNotFound_shouldThrowBucketNotFoundException() {
        given(repository.findById(bucketId)).willReturn(Optional.empty());

        assertThrows(BucketNotFoundException.class, () -> service.deleteBucket(bucketId));

        verify(repository, never()).deleteById(any());
    }
}
