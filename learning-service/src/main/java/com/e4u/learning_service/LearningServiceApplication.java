package com.e4u.learning_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Learning Service.
 * 
 * <p>
 * This service combines both lesson and curriculum management functionality
 * for the e4u application. It handles:
 * <ul>
 * <li>Curriculum management (curricula, units, goals, dictionaries)</li>
 * <li>Lesson management (dynamic lessons, exercises, vocabulary)</li>
 * <li>User learning progress tracking</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
// @EnableFeignClients(basePackages = "com.e4u.learning_service.client")
public class LearningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningServiceApplication.class, args);
    }

}
