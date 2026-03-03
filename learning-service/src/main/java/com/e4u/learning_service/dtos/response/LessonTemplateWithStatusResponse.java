package com.e4u.learning_service.dtos.response;

import com.e4u.learning_service.entities.LessonTemplate.LessonType;
import com.e4u.learning_service.entities.UserLessonSession.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response model combining LessonTemplate with user's session status.
 * Provides lesson info along with the user's learning progress.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonTemplateWithStatusResponse {

    // ============ Lesson Template Fields ============

    private UUID id;

    private UUID unitId;

    private String lessonName;

    private LessonType lessonType;

    private Integer sequenceOrder;

    private Integer exerciseCount;

    private Instant createdAt;

    private Instant updatedAt;

    // ============ User Session Status Fields ============

    /**
     * User's session ID for this lesson (null if not started)
     */
    private UUID sessionId;

    /**
     * Computed display status based on session data.
     * Possible values: NOT_STARTED, IN_PROGRESS, COMPLETED
     */
    private LessonDisplayStatus displayStatus;

    /**
     * Session status from the actual session (null if no session exists)
     */
    private SessionStatus sessionStatus;

    /**
     * Progress percentage (0-100)
     */
    private Integer progressPercentage;

    /**
     * Number of completed exercises in the session
     */
    private Integer completedItems;

    /**
     * Accuracy rate as percentage (0-100), null if no attempts yet
     */
    private Integer accuracyPercentage;

    /**
     * Whether this lesson can be resumed (has an active session)
     */
    private Boolean canResume;

    /**
     * Display status enum for frontend consumption
     */
    public enum LessonDisplayStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED;

        /**
         * Derive display status from session status
         */
        public static LessonDisplayStatus fromSessionStatus(SessionStatus sessionStatus) {
            if (sessionStatus == null) {
                return NOT_STARTED;
            }
            switch (sessionStatus) {
                case COMPLETED:
                    return COMPLETED;
                case IN_PROGRESS:
                case PAUSED:
                    return IN_PROGRESS;
                case NOT_STARTED:
                default:
                    return NOT_STARTED;
            }
        }
    }

    /**
     * Builder helper to create from LessonTemplateResponse without session
     */
    public static LessonTemplateWithStatusResponse fromTemplateOnly(LessonTemplateResponse template) {
        return LessonTemplateWithStatusResponse.builder()
                .id(template.getId())
                .unitId(template.getUnitId())
                .lessonName(template.getLessonName())
                .lessonType(template.getLessonType())
                .sequenceOrder(template.getSequenceOrder())
                .exerciseCount(template.getExerciseCount())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .sessionId(null)
                .displayStatus(LessonDisplayStatus.NOT_STARTED)
                .sessionStatus(null)
                .progressPercentage(0)
                .completedItems(0)
                .accuracyPercentage(null)
                .canResume(false)
                .build();
    }

    /**
     * Builder helper to create from template and session
     */
    public static LessonTemplateWithStatusResponse fromTemplateAndSession(
            LessonTemplateResponse template,
            UserLessonSessionResponse session) {

        if (session == null) {
            return fromTemplateOnly(template);
        }

        Integer progressPercent = 0;
        if (template.getExerciseCount() != null && template.getExerciseCount() > 0
                && session.getCompletedItems() != null) {
            progressPercent = (int) ((session.getCompletedItems() * 100.0) / template.getExerciseCount());
        }

        Integer accuracyPercent = null;
        if (session.getAccuracyRate() != null) {
            accuracyPercent = (int) (session.getAccuracyRate() * 100);
        }

        boolean canResume = session.getStatus() == SessionStatus.IN_PROGRESS
                || session.getStatus() == SessionStatus.PAUSED;

        return LessonTemplateWithStatusResponse.builder()
                .id(template.getId())
                .unitId(template.getUnitId())
                .lessonName(template.getLessonName())
                .lessonType(template.getLessonType())
                .sequenceOrder(template.getSequenceOrder())
                .exerciseCount(template.getExerciseCount())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .sessionId(session.getId())
                .displayStatus(LessonDisplayStatus.fromSessionStatus(session.getStatus()))
                .sessionStatus(session.getStatus())
                .progressPercentage(progressPercent)
                .completedItems(session.getCompletedItems())
                .accuracyPercentage(accuracyPercent)
                .canResume(canResume)
                .build();
    }
}
