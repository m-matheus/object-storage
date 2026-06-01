package com.matheus.object_storage.bucket.dto;

import java.time.Instant;
import java.util.UUID;

public record BucketResponse(UUID id, String name, String ownerId, Instant createdAt) {}
