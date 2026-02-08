package com.renting.item_service.common.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class CustomJwtException extends RuntimeException{
    public CustomJwtException(String message, Throwable rootCause) {
        super(message, rootCause);}

    public CustomJwtException(String message) {
        super(message);}
}
