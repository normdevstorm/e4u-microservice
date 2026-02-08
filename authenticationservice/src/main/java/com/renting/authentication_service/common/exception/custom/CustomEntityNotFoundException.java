package com.renting.authentication_service.common.exception.custom;

public class CustomEntityNotFoundException extends RuntimeException {
    public CustomEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomEntityNotFoundException(String message) {
        super(message);
    }
}
