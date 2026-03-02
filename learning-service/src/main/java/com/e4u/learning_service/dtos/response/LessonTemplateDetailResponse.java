package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.LessonTemplate.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Detailed response model for LessonTemplate including exercises.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonTemplateDetailResponse {

    private UUID id;

    private UUID unitId;

    private String unitName;

    private String lessonName;

    private LessonType lessonType;

    private Integer sequenceOrder;

    private List<ExerciseTemplateResponse> exercises;

    private Instant createdAt;

    private Instant updatedAt;
}
