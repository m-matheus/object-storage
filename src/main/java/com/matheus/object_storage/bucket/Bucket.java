package com.matheus.object_storage.bucket;

import java.time.Instant;
import java.util.UUID;

public record Bucket(UUID id, String name, String ownerId, Instant createdAt) {}
