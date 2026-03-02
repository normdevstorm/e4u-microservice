package com.e4u.learning_service.dtos.request;

import com.e4u.learning_service.entities.LessonTemplate.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for updating a LessonTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonTemplateUpdateRequest {

    private String lessonName;

    private LessonType lessonType;

    private Integer sequenceOrder;
}
