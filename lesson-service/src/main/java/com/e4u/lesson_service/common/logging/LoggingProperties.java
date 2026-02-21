package com.e4u.lesson_service.common.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration properties for logging behavior.
 * Allows fine-grained control over what gets logged.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.logging")
public class LoggingProperties {

    /**
     * Enable/disable request-response logging
     */
    private boolean enabled = true;

    /**
     * Log request body
     */
    private boolean logRequestBody = true;

    /**
     * Log response body
     */
    private boolean logResponseBody = true;

    /**
     * Maximum length for request body in logs
     */
    private int maxRequestBodyLength = 1000;

    /**
     * Maximum length for response body in logs
     */
    private int maxResponseBodyLength = 2000;

    /**
     * Headers to include in logs (empty = none)
     */
    private Set<String> includedHeaders = new HashSet<>();

    /**
     * Sensitive headers to mask (e.g., Authorization)
     */
    private Set<String> sensitiveHeaders = Set.of(
            "authorization",
            "x-api-key",
            "cookie",
            "set-cookie");

    /**
     * Fields to mask in request/response body
     */
    private Set<String> sensitiveFields = Set.of(
            "password",
            "secret",
            "token",
            "apiKey",
            "api_key",
            "creditCard",
            "credit_card",
            "ssn");

    /**
     * URI patterns to exclude from logging (e.g., health checks)
     */
    private Set<String> excludedUriPatterns = Set.of(
            "/actuator/**",
            "/health",
            "/ready",
            "/live");

    /**
     * Slow request threshold in milliseconds
     */
    private long slowRequestThresholdMs = 3000;
}
