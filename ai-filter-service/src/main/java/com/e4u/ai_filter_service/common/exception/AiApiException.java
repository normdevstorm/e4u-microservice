package com.e4u.ai_filter_service.common.exception;

/**
 * Thrown when the AI API returns an error or an unexpected response.
 * This exception is configured as retryable in the Spring Batch step
 * fault-tolerance settings.
 */
public class AiApiException extends RuntimeException {

    public AiApiException(String message) {
        super(message);
    }

    public AiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
