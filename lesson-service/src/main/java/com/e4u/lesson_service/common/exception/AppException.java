package com.e4u.lesson_service.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Application exception class for all business exceptions.
 * Contains error code, message, and optional detail for debugging.
 */
@Getter
public class AppException extends RuntimeException {

    private final String code;
    private final String detail;
    private final HttpStatus httpStatus;

    public AppException(String code, String message) {
        super(message);
        this.code = code;
        this.detail = null;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AppException(String code, String message, String detail) {
        super(message);
        this.code = code;
        this.detail = detail;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AppException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.detail = null;
        this.httpStatus = httpStatus;
    }

    public AppException(String code, String message, String detail, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.detail = detail;
        this.httpStatus = httpStatus;
    }

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.detail = null;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public AppException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.detail = detail;
        this.httpStatus = errorCode.getHttpStatus();
    }
}
