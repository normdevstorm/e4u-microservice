package com.e4u.curriculum_service.common.exception;

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

    // Goal errors (3xxx)
    GOAL_NOT_FOUND("ERR_3000", "Goal definition not found", HttpStatus.NOT_FOUND),
    USER_GOAL_NOT_FOUND("ERR_3001", "User goal not found", HttpStatus.NOT_FOUND),
    USER_GOAL_ALREADY_EXISTS("ERR_3002", "User already has this goal", HttpStatus.CONFLICT),

    // Curriculum errors (4xxx)
    CURRICULUM_NOT_FOUND("ERR_4000", "Curriculum not found", HttpStatus.NOT_FOUND),
    CURRICULUM_UNIT_NOT_FOUND("ERR_4001", "Curriculum unit not found", HttpStatus.NOT_FOUND),
    UNIT_BASE_WORD_NOT_FOUND("ERR_4002", "Unit base word not found", HttpStatus.NOT_FOUND),

    // Dictionary errors (5xxx)
    GLOBAL_DICTIONARY_NOT_FOUND("ERR_5100", "Word not found in dictionary", HttpStatus.NOT_FOUND),
    TRANSLATION_NOT_FOUND("ERR_5101", "Translation not found", HttpStatus.NOT_FOUND),
    TRANSLATION_ALREADY_EXISTS("ERR_5102", "Translation for this language already exists", HttpStatus.CONFLICT),

    // Data integrity errors (6xxx)
    DATA_INTEGRITY_VIOLATION("ERR_6000", "Data integrity violation", HttpStatus.CONFLICT),
    OPTIMISTIC_LOCK_FAILURE("ERR_6001", "Resource was modified by another user", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
