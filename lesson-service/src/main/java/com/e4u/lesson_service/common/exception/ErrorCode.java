package com.e4u.lesson_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum containing all application error codes.
 * Provides consistent error handling across the application.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // General errors (1xxx)
    INTERNAL_SERVER_ERROR("ERR_1000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("ERR_1001", "Bad request", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("ERR_1002", "Validation failed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("ERR_1003", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_1004", "Access forbidden", HttpStatus.FORBIDDEN),
    METHOD_NOT_ALLOWED("ERR_1005", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),

    // Resource errors (2xxx)
    RESOURCE_NOT_FOUND("ERR_2000", "Resource not found", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS("ERR_2001", "Resource already exists", HttpStatus.CONFLICT),
    RESOURCE_DELETED("ERR_2002", "Resource has been deleted", HttpStatus.GONE),

    // User Vocab Instance errors (3xxx)
    VOCAB_INSTANCE_NOT_FOUND("ERR_3000", "Vocabulary instance not found", HttpStatus.NOT_FOUND),
    VOCAB_INSTANCE_ALREADY_EXISTS("ERR_3001", "Vocabulary instance already exists", HttpStatus.CONFLICT),

    // Lesson errors (4xxx)
    LESSON_NOT_FOUND("ERR_4000", "Lesson not found", HttpStatus.NOT_FOUND),
    LESSON_EXERCISE_NOT_FOUND("ERR_4001", "Lesson exercise not found", HttpStatus.NOT_FOUND),

    // Data integrity errors (5xxx)
    DATA_INTEGRITY_VIOLATION("ERR_5000", "Data integrity violation", HttpStatus.CONFLICT),
    OPTIMISTIC_LOCK_FAILURE("ERR_5001", "Resource was modified by another user", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
