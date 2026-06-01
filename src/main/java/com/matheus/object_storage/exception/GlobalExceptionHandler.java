package com.matheus.object_storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.matheus.object_storage.bucket.BucketAlreadyExistsException;
import com.matheus.object_storage.bucket.BucketNotFoundException;
import com.matheus.object_storage.object.ObjectNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BucketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBucketNotFound(BucketNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BucketAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBucketAlreadyExists(BucketAlreadyExistsException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(ObjectNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
