package com.e4u.lesson_service.services;

import com.e4u.lesson_service.models.request.LessonExerciseCreateRequest;
import com.e4u.lesson_service.models.request.LessonExerciseFilterRequest;
import com.e4u.lesson_service.models.request.LessonExerciseUpdateRequest;
import com.e4u.lesson_service.models.response.LessonExerciseDetailResponse;
import com.e4u.lesson_service.models.response.LessonExerciseResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for LessonExercise operations.
 */
public interface LessonExerciseService {

    /**
     * Get all lesson exercises (paginated)
     */
    Page<LessonExerciseResponse> getAll(int page, int size, String sortBy, String sortDirection);

    /**
     * Get lesson exercise by ID
     */
    LessonExerciseResponse getById(UUID id);

    /**
     * Get lesson exercise by ID with vocab details
     */
    LessonExerciseDetailResponse getByIdWithDetails(UUID id);

    /**
     * Get all exercises for a vocab instance
     */
    List<LessonExerciseResponse> getByVocabId(UUID wordInstanceId);

    /**
     * Get all exercises for a vocab instance (paginated)
     */
    Page<LessonExerciseResponse> getByVocabId(UUID wordInstanceId, int page, int size, String sortBy,
            String sortDirection);

    /**
     * Get all exercises for a lesson
     */
    List<LessonExerciseResponse> getByLessonId(UUID lessonId);

    /**
     * Get all exercises for a lesson (paginated)
     */
    Page<LessonExerciseResponse> getByLessonId(UUID lessonId, int page, int size, String sortBy, String sortDirection);

    /**
     * Get all exercises for a unit
     */
    List<LessonExerciseResponse> getByUnitId(UUID unitId);

    /**
     * Get all exercises for a unit (paginated)
     */
    Page<LessonExerciseResponse> getByUnitId(UUID unitId, int page, int size, String sortBy, String sortDirection);

    /**
     * Filter lesson exercises with criteria
     */
    Page<LessonExerciseResponse> filter(LessonExerciseFilterRequest filterRequest);

    /**
     * Create a batch of lesson exercises
     */
    List<LessonExerciseResponse> createBatch(List<LessonExerciseCreateRequest> requests);

    /**
     * Partially update a lesson exercise
     */
    LessonExerciseResponse partialUpdate(UUID id, LessonExerciseUpdateRequest request);

    /**
     * Soft delete a lesson exercise
     */
    void softDelete(UUID id);

    /**
     * Soft delete multiple lesson exercises
     */
    void softDeleteBatch(List<UUID> ids);
}
