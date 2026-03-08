package com.e4u.ai_filter_service.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI API configuration properties.
 * Mapped from {@code ai.api.*} in ai-filter-service.yml.
 * Registered as a bean via {@code @EnableConfigurationProperties} in AppConfig.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ai.api")
public class AiApiProperties {

    /** Base URL of the AI provider endpoint (e.g. https://api.openai.com). */
    private String url;

    /** API key / Bearer token. Injected from environment variable AI_API_KEY. */
    private String key;

    /** Model identifier (e.g. gpt-4o-mini, gpt-4o). */
    private String model = "gpt-4o-mini";

    /** HTTP request timeout in seconds. */
    private int timeoutSeconds = 30;

    /**
     * Number of words packed into a single AI API call.
     * Larger = fewer calls but higher token usage per request.
     */
    private int batchSize = 20;
}
