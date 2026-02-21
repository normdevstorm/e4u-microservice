package com.e4u.lesson_service.services;

import com.e4u.lesson_service.models.request.DynamicLessonCreateRequest;
import com.e4u.lesson_service.models.request.DynamicLessonFilterRequest;
import com.e4u.lesson_service.models.request.DynamicLessonUpdateRequest;
import com.e4u.lesson_service.models.response.DynamicLessonDetailResponse;
import com.e4u.lesson_service.models.response.DynamicLessonResponse;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for DynamicLesson operations.
 */
public interface DynamicLessonService {

    /**
     * Get all dynamic lessons (paginated)
     */
    Page<DynamicLessonResponse> getAll(int page, int size, String sortBy, String sortDirection);

    /**
     * Get dynamic lesson by ID
     */
    DynamicLessonResponse getById(UUID id);

    /**
     * Get dynamic lesson by ID with full vocab details
     */
    DynamicLessonDetailResponse getByIdWithDetails(UUID id);

    /**
     * Get all dynamic lessons for a user
     */
    List<DynamicLessonResponse> getByUserId(UUID userId);

    /**
     * Get all dynamic lessons for a user (paginated)
     */
    Page<DynamicLessonResponse> getByUserId(UUID userId, int page, int size, String sortBy, String sortDirection);

    /**
     * Get all dynamic lessons for a unit (UserUnitState)
     */
    List<DynamicLessonResponse> getByUnitId(UUID userUnitStateId);

    /**
     * Get all dynamic lessons for a unit (paginated)
     */
    Page<DynamicLessonResponse> getByUnitId(UUID userUnitStateId, int page, int size, String sortBy,
            String sortDirection);

    /**
     * Filter dynamic lessons with criteria
     */
    Page<DynamicLessonResponse> filter(DynamicLessonFilterRequest filterRequest);

    /**
     * Get all vocab instances for a dynamic lesson
     */
    List<UserVocabInstanceResponse> getAllVocabByDynamicLesson(UUID lessonId);

    /**
     * Create a new dynamic lesson with vocab instances
     */
    DynamicLessonResponse create(DynamicLessonCreateRequest request);

    /**
     * Partially update a dynamic lesson
     */
    DynamicLessonResponse partialUpdate(UUID id, DynamicLessonUpdateRequest request);

    /**
     * Soft delete a dynamic lesson
     */
    void softDelete(UUID id);
}
