package com.matheus.object_storage.bucket;

public class BucketNotFoundException extends RuntimeException {
    public BucketNotFoundException(String message) {
        super(message);
    }
}