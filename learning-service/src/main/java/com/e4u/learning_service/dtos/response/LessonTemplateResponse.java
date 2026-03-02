package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.LessonTemplate.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response model for LessonTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonTemplateResponse {

    private UUID id;

    private UUID unitId;

    private String lessonName;

    private LessonType lessonType;

    private Integer sequenceOrder;

    private Integer exerciseCount;

    private Instant createdAt;

    private Instant updatedAt;
}
