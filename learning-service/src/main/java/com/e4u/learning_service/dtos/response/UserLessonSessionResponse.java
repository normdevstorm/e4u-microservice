package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.UserLessonSession.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response model for UserLessonSession.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonSessionResponse {

    private UUID id;

    private UUID userId;

    private UUID lessonTemplateId;

    private String lessonName;

    private UUID userUnitStateId;

    private SessionStatus status;

    private Integer totalItems;

    private Integer completedItems;

    private Integer correctItems;

    private Float accuracyRate;

    private Instant createdAt;

    private Instant updatedAt;
}
