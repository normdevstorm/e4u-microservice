package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.UserExerciseAttemptRequest;
import com.e4u.learning_service.dtos.response.UserExerciseAttemptResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing user exercise attempts.
 * Handles answer submission and validation.
 */
public interface UserExerciseAttemptService {

    /**
     * Submit an exercise attempt
     * Validates answer, records attempt, updates session progress
     */
    UserExerciseAttemptResponse submitAttempt(UserExerciseAttemptRequest request);

    /**
     * Get attempt by ID
     */
    UserExerciseAttemptResponse getAttemptById(UUID attemptId);

    /**
     * Get all attempts for a session
     */
    List<UserExerciseAttemptResponse> getAttemptsBySession(UUID sessionId);

    /**
     * Get all attempts for a specific exercise in a session
     */
    List<UserExerciseAttemptResponse> getAttemptsBySessionAndExercise(UUID sessionId, UUID exerciseTemplateId);

    /**
     * Get all attempts for a user
     */
    List<UserExerciseAttemptResponse> getAttemptsByUser(UUID userId);

    /**
     * Get recent attempts for a user (for analytics)
     */
    List<UserExerciseAttemptResponse> getRecentAttemptsByUser(UUID userId, int limit);

    /**
     * Calculate accuracy for a session
     */
    float calculateSessionAccuracy(UUID sessionId);

    /**
     * Calculate accuracy for a user across all sessions
     */
    float calculateUserAccuracy(UUID userId);

    /**
     * Delete an attempt (admin only)
     */
    void deleteAttempt(UUID attemptId);
}
