package com.e4u.lesson_service.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an external service call fails.
 */
public class ServiceException extends AppException {

    public ServiceException(String serviceName, String message) {
        super(
                ErrorCode.EXTERNAL_SERVICE_ERROR.getCode(),
                String.format("Error calling %s: %s", serviceName, message),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceException(String message) {
        super(ErrorCode.EXTERNAL_SERVICE_ERROR.getCode(), message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ServiceException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
