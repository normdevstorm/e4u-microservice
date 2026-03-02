package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.LessonTemplateCreateRequest;
import com.e4u.learning_service.dtos.request.LessonTemplateUpdateRequest;
import com.e4u.learning_service.dtos.response.LessonTemplateDetailResponse;
import com.e4u.learning_service.dtos.response.LessonTemplateResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing LessonTemplate operations.
 * Part of the blueprint layer - handles static lesson definitions.
 */
public interface LessonTemplateService {

    /**
     * Create a new lesson template
     */
    LessonTemplateResponse createLessonTemplate(LessonTemplateCreateRequest request);

    /**
     * Get lesson template by ID
     */
    LessonTemplateResponse getLessonTemplateById(UUID id);

    /**
     * Get detailed lesson template by ID (includes exercises)
     */
    LessonTemplateDetailResponse getLessonTemplateDetail(UUID id);

    /**
     * Get all lesson templates for a curriculum unit
     */
    List<LessonTemplateResponse> getLessonTemplatesByUnit(UUID unitId);

    /**
     * Get all lesson templates for a curriculum unit with user session status
     * @param unitId The curriculum unit ID
     * @param userId The user ID to check session status for
     */
    List<LessonTemplateResponse> getLessonTemplatesByUnitWithUserStatus(UUID unitId, UUID userId);

    /**
     * Update a lesson template
     */
    LessonTemplateResponse updateLessonTemplate(UUID id, LessonTemplateUpdateRequest request);

    /**
     * Delete a lesson template
     */
    void deleteLessonTemplate(UUID id);

    /**
     * Reorder lessons within a unit
     */
    void reorderLessons(UUID unitId, List<UUID> lessonIds);
}
