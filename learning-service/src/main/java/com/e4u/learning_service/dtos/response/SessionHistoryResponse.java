package com.e4u.learning_service.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * Response DTO for a single completed learning session entry.
 * Used in the paginated session-history list on the Flutter stats screen.
 */
@Data
@Builder
public class SessionHistoryResponse {

    /** UUID of the UserLessonSession. */
    private String id;

    /** ISO-8601 datetime string of when the session was created (UTC). */
    private String startTime;

    /** Total duration in minutes (sum of attempt timeTakenSeconds / 60). */
    private int durationMinutes;

    /** Title of the lesson template (displayed in the session list). */
    private String unitTitle;

    /** Total items (exercises) in the session. */
    private int wordsStudied;

    /** Accuracy rate normalised to 0.0–1.0 (accuracyRate / 100). */
    private double accuracy;

    /**
     * Study phase identifier — "STUDY" for standard sessions; extensible for future
     * phases.
     */
    private String completedPhase;
}
