package com.e4u.learning_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum containing all application error codes for the Learning Service.
 * Combines error codes from both lesson and curriculum domains.
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

    // Vocabulary Instance errors (3xxx) - Lesson domain
    VOCAB_INSTANCE_NOT_FOUND("ERR_3000", "Vocabulary instance not found", HttpStatus.NOT_FOUND),
    VOCAB_INSTANCE_ALREADY_EXISTS("ERR_3001", "Vocabulary instance already exists", HttpStatus.CONFLICT),

    // Goal errors (3xxx) - Curriculum domain
    GOAL_NOT_FOUND("ERR_3100", "Goal definition not found", HttpStatus.NOT_FOUND),
    USER_GOAL_NOT_FOUND("ERR_3101", "User goal not found", HttpStatus.NOT_FOUND),
    USER_GOAL_ALREADY_EXISTS("ERR_3102", "User already has this goal", HttpStatus.CONFLICT),

    // Lesson errors (4xxx) - Lesson domain
    LESSON_NOT_FOUND("ERR_4000", "Lesson not found", HttpStatus.NOT_FOUND),
    LESSON_EXERCISE_NOT_FOUND("ERR_4001", "Lesson exercise not found", HttpStatus.NOT_FOUND),

    // Curriculum errors (4xxx) - Curriculum domain
    CURRICULUM_NOT_FOUND("ERR_4100", "Curriculum not found", HttpStatus.NOT_FOUND),
    CURRICULUM_UNIT_NOT_FOUND("ERR_4101", "Curriculum unit not found", HttpStatus.NOT_FOUND),
    UNIT_BASE_WORD_NOT_FOUND("ERR_4102", "Unit base word not found", HttpStatus.NOT_FOUND),

    // Unit errors (4xxx)
    UNIT_NOT_FOUND("ERR_4200", "Unit not found", HttpStatus.NOT_FOUND),
    USER_UNIT_STATE_NOT_FOUND("ERR_4201", "User unit state not found", HttpStatus.NOT_FOUND),

    // Dictionary errors (5xxx) - Curriculum domain
    GLOBAL_DICTIONARY_NOT_FOUND("ERR_5100", "Word not found in dictionary", HttpStatus.NOT_FOUND),
    TRANSLATION_NOT_FOUND("ERR_5101", "Translation not found", HttpStatus.NOT_FOUND),
    TRANSLATION_ALREADY_EXISTS("ERR_5102", "Translation for this language already exists", HttpStatus.CONFLICT),

    // Data integrity errors (6xxx)
    DATA_INTEGRITY_VIOLATION("ERR_6000", "Data integrity violation", HttpStatus.CONFLICT),
    OPTIMISTIC_LOCK_FAILURE("ERR_6001", "Resource was modified by another user", HttpStatus.CONFLICT),

    // Profile errors (8xxx)
    USER_PROFILE_NOT_FOUND("ERR_8000", "User profile not found", HttpStatus.NOT_FOUND),
    USER_PROFILE_ALREADY_EXISTS("ERR_8001", "User profile already exists", HttpStatus.CONFLICT),
    GOAL_LIMIT_EXCEEDED("ERR_8002", "Maximum of 3 goals allowed per user", HttpStatus.UNPROCESSABLE_ENTITY),

    // External service errors (7xxx)
    EXTERNAL_SERVICE_ERROR("ERR_7000", "External service error", HttpStatus.SERVICE_UNAVAILABLE),
    ACCOUNT_SERVICE_ERROR("ERR_7001", "Account service error", HttpStatus.SERVICE_UNAVAILABLE);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
