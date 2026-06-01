package com.matheus.object_storage.object.dto;

import java.time.Instant;
import java.util.UUID;

public record ObjectResponse(UUID id, String key, UUID bucketId, String contentType, long size, Instant createdAt) {}