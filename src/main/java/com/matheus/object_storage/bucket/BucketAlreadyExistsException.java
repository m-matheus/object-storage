package com.matheus.object_storage.bucket;

public class BucketAlreadyExistsException extends RuntimeException{
    public BucketAlreadyExistsException(String message) {
        super(message);
    }
}
