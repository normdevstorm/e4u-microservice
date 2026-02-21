package com.e4u.lesson_service.models.request;

import com.e4u.lesson_service.entities.DynamicLesson.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request model for partially updating a DynamicLesson.
 * All fields are optional - only provided fields will be updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicLessonUpdateRequest {

    private UUID userUnitStateId;

    private LessonStatus status;

    private Float accuracyRate;

    private Integer completedItems;

    /**
     * Add new vocab instance IDs to the lesson
     */
    private List<UUID> addVocabInstanceIds;

    /**
     * Remove vocab instance IDs from the lesson
     */
    private List<UUID> removeVocabInstanceIds;
}
