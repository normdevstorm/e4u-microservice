package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.ExerciseTemplateCreateRequest;
import com.e4u.learning_service.dtos.request.ExerciseTemplateUpdateRequest;
import com.e4u.learning_service.dtos.response.ExerciseTemplateResponse;
import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing ExerciseTemplate operations.
 * Handles both shared templates and user-specific generated exercises.
 */
public interface ExerciseTemplateService {

    /**
     * Create a new exercise template
     */
    ExerciseTemplateResponse createExerciseTemplate(ExerciseTemplateCreateRequest request);

    /**
     * Get exercise template by ID
     */
    ExerciseTemplateResponse getExerciseTemplateById(UUID id);

    /**
     * Get all exercises for a lesson template
     * Returns shared templates + user-specific exercises if userId provided
     */
    List<ExerciseTemplateResponse> getExercisesForLesson(UUID lessonTemplateId, UUID userId);

    /**
     * Get exercises for a lesson without exposing correct answers (for learning)
     */
    List<ExerciseTemplateResponse> getExercisesForLearning(UUID lessonTemplateId, UUID userId);

    /**
     * Update an exercise template
     */
    ExerciseTemplateResponse updateExerciseTemplate(UUID id, ExerciseTemplateUpdateRequest request);

    /**
     * Delete an exercise template
     */
    void deleteExerciseTemplate(UUID id);

    /**
     * Generate exercises for a user based on their vocab progress
     * Creates user-specific exercises for review sessions
     */
    List<ExerciseTemplateResponse> generateReviewExercises(UUID userId, UUID lessonTemplateId, List<UUID> wordIds);

    /**
     * Generate a specific type of exercise for a word
     */
    ExerciseTemplateResponse generateExerciseForWord(UUID wordId, ExerciseType exerciseType, UUID userId);

    /**
     * Get exercises by type for a lesson
     */
    List<ExerciseTemplateResponse> getExercisesByType(UUID lessonTemplateId, ExerciseType exerciseType);
}
