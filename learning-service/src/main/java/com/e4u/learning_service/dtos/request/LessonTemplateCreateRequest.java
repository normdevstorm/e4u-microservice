package com.e4u.learning_service.dtos.request;

import com.e4u.learning_service.entities.LessonTemplate.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for creating a new LessonTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonTemplateCreateRequest {

    @NotNull(message = "Unit ID is required")
    private UUID unitId;

    @NotBlank(message = "Lesson name is required")
    private String lessonName;

    @Builder.Default
    private LessonType lessonType = LessonType.STANDARD;

    private Integer sequenceOrder;
}
