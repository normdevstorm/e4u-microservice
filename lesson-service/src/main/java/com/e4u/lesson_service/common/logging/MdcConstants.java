package com.e4u.lesson_service.common.logging;

/**
 * MDC (Mapped Diagnostic Context) key constants.
 * These keys are used for structured logging and are compatible with
 * OpenTelemetry and ELK Stack conventions.
 */
public final class MdcConstants {

    private MdcConstants() {
        // Prevent instantiation
    }

    // OpenTelemetry trace context
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_SPAN_ID = "parentSpanId";

    // Request context
    public static final String REQUEST_ID = "requestId";
    public static final String CORRELATION_ID = "correlationId";

    // HTTP context
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_URI = "httpUri";
    public static final String HTTP_STATUS = "httpStatus";

    // Client context
    public static final String CLIENT_IP = "clientIp";
    public static final String USER_ID = "userId";
    public static final String USER_AGENT = "userAgent";

    // Application context
    public static final String SERVICE_NAME = "serviceName";
    public static final String ENVIRONMENT = "environment";

    // Performance
    public static final String DURATION_MS = "durationMs";

    // Error context
    public static final String ERROR_TYPE = "errorType";
    public static final String ERROR_MESSAGE = "errorMessage";
}
