package com.matheus.object_storage.object;

import java.time.Instant;
import java.util.UUID;

public record BucketObject(UUID id, String key, UUID bucketId, String contentType, long size, Instant createdAt, byte[] data) {}
