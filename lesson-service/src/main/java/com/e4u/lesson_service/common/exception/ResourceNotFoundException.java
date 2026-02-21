package com.e4u.lesson_service.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                String.format("%s not found with id: %s", resourceName, resourceId),
                HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND.getCode(), message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
