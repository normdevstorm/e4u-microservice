package com.e4u.curriculum_service.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for OpenFeign clients used for inter-service communication.
 * 
 * <p>
 * <b>Security Considerations:</b>
 * </p>
 * <ul>
 * <li>Configure proper timeouts to prevent resource exhaustion</li>
 * <li>Implement circuit breaker pattern for fault tolerance</li>
 * <li>Use HTTPS in production environments</li>
 * </ul>
 * 
 * <p>
 * <b>Performance Considerations:</b>
 * </p>
 * <ul>
 * <li>Connection pooling is handled by the underlying HTTP client</li>
 * <li>Retry logic should be tuned based on service SLAs</li>
 * </ul>
 */
@Configuration
@EnableFeignClients(basePackages = "com.e4u.curriculum_service.client")
public class FeignConfig {

    /**
     * Configure Feign logging level.
     * FULL level logs headers, body, and metadata.
     * Use BASIC in production for performance.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Configure request options (timeouts).
     * These values should be tuned based on downstream service SLAs.
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5, TimeUnit.SECONDS, // Connect timeout
                10, TimeUnit.SECONDS, // Read timeout
                true // Follow redirects
        );
    }

    /**
     * Configure retry policy for transient failures.
     * Uses exponential backoff strategy.
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                100, // Initial interval in ms
                1000, // Max interval in ms
                3 // Max attempts
        );
    }
}
