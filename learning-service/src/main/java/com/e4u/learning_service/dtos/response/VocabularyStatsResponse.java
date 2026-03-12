package com.e4u.learning_service.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response DTO for vocabulary distribution statistics.
 * Drives the donut / pie chart on the Flutter stats screen.
 */
@Data
@Builder
public class VocabularyStatsResponse {

    /** Words where isMastered = true. */
    private long mastered;

    /**
     * Words with intervalDays > 0, not mastered, and not yet overdue (nextReviewAt
     * > now).
     */
    private long learning;

    /** Words that are overdue for review (nextReviewAt <= now, not mastered). */
    private long needsReview;

    /** Words never reviewed by SRS (intervalDays = 0). */
    private long newWords;
}
