package com.e4u.lesson_service.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_FAILED.getCode(), message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message, String detail) {
        super(ErrorCode.VALIDATION_FAILED.getCode(), message, detail, HttpStatus.BAD_REQUEST);
    }
}
