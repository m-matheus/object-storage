package com.matheus.object_storage.bucket;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.matheus.object_storage.bucket.dto.BucketRequest;
import com.matheus.object_storage.bucket.dto.BucketResponse;


@RestController
@RequestMapping("/buckets")
public class BucketController {
    private final BucketService bucketService;

    private BucketResponse toResponse(Bucket bucket) {
        return new BucketResponse(bucket.id(), bucket.name(), bucket.ownerId(), bucket.createdAt());
    }

    public BucketController(BucketService service) {
        this.bucketService = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BucketResponse create(@RequestBody BucketRequest request) {
        Bucket bucket = bucketService.createBucket(request.name());

        return toResponse(bucket);
    }

    @GetMapping
    public List<BucketResponse> list() {
        List<Bucket> buckets = bucketService.listBuckets();

        return buckets.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public BucketResponse get(@PathVariable UUID id) {
        return toResponse(bucketService.getBucket(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        bucketService.deleteBucket(id);
    }
}
