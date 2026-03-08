package com.e4u.ai_filter_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the AI Filter Service.
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Scheduled nightly batch job — reads words from {@code e4u_learning},
 * calls AI API,
 * saves results to {@code e4u_ai_filter}.</li>
 * <li>REST API for manual job triggering and status monitoring.</li>
 * </ul>
 *
 * <p>
 * NOTE: {@code @EnableBatchProcessing} is declared in
 * {@link com.e4u.ai_filter_service.config.batch.BatchConfig}
 * (not here) so it can be explicitly pointed to the {@code @Primary}
 * datasource.
 */
@SpringBootApplication
@EnableScheduling
public class AiFilterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiFilterServiceApplication.class, args);
	}
}
