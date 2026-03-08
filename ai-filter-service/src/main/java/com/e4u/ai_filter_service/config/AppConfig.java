package com.e4u.ai_filter_service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.e4u.ai_filter_service.common.properties.AiApiProperties;
import com.e4u.ai_filter_service.common.properties.BatchProperties;

import java.time.Duration;

/**
 * General application-wide beans.
 */
@Configuration
@EnableConfigurationProperties({ AiApiProperties.class, BatchProperties.class })
public class AppConfig {

    /**
     * Shared ObjectMapper with JavaTime support and lenient deserialization.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * RestClient pre-configured for the AI API endpoint.
     * Timeout and base URL are driven by {@link AiApiProperties}.
     */
    @Bean
    @Qualifier("aiRestClient")
    public RestClient aiRestClient(AiApiProperties aiApiProperties) {
        return RestClient.builder()
                .baseUrl(aiApiProperties.getUrl())
                .defaultHeader("Authorization", "Bearer " + aiApiProperties.getKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
