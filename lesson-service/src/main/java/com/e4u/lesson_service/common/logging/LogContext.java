package com.e4u.lesson_service.common.logging;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * Structured log context for request/response logging.
 * Designed for ELK Stack and OpenTelemetry integration.
 */
@Data
@Builder
public class LogContext {

    // Trace identifiers (OpenTelemetry compatible)
    private String traceId;
    private String spanId;
    private String parentSpanId;

    // Request identifiers
    private String requestId;
    private String correlationId;

    // HTTP context
    private String httpMethod;
    private String uri;
    private String queryString;
    private Integer httpStatus;

    // Client information
    private String clientIp;
    private String userAgent;
    private String userId;

    // Application context
    private String serviceName;
    private String environment;
    private String version;

    // Method context
    private String className;
    private String methodName;

    // Timing
    private Instant timestamp;
    private Long durationMs;

    // Payload (optional, can be disabled for sensitive data)
    private Object requestBody;
    private Object responseBody;

    // Error context
    private String errorType;
    private String errorMessage;
    private String stackTrace;

    // Custom attributes
    private Map<String, Object> attributes;
}
