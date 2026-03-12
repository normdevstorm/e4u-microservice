package com.e4u.learning_service.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response DTO for a single day's learning activity.
 * 7 of these form the weekly bar-chart data on the Flutter stats screen.
 */
@Data
@Builder
public class LearningActivityResponse {

    /** Calendar date in ISO-8601 format "yyyy-MM-dd" (UTC). */
    private String date;

    /** Sum of correctItems across all completed sessions that day. */
    private int wordsLearned;

    /**
     * Sum of exercise timeTakenSeconds / 60 for that day (0 until attempt-level
     * tracking is added).
     */
    private int studyTimeMinutes;

    /** Count of completed sessions for that day. */
    private int sessionsCompleted;

    /** Average accuracyRate of sessions that day, normalised to 0.0–1.0. */
    private double accuracy;
}
