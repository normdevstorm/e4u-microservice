package com.e4u.learning_service.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response DTO carrying aggregated learning statistics for a user.
 * Consumed by the Flutter stats overview screen.
 */
@Data
@Builder
public class UserStatsResponse {

    /**
     * Number of consecutive days with at least one completed session (current run).
     */
    private int currentStreak;

    /** All-time best streak (days). */
    private int longestStreak;

    /** Count of words where isMastered = true in UserVocabProgress. */
    private int totalWordsLearned;

    /** Sum of exercise timeTakenSeconds / 60 across all completed sessions. */
    private long totalStudyTimeMinutes;

    /** Average accuracyRate of all completed sessions, normalised to 0.0–1.0. */
    private double overallAccuracy;

    /**
     * ISO-8601 date string "yyyy-MM-dd" of the most-recent completed session (UTC).
     */
    private String lastStudyDate;
}
