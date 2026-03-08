package com.e4u.ai_filter_service.batch.domain;

import java.util.List;

/**
 * Snapshot of a learner's profile at the time of batch processing.
 *
 * <p>
 * Aggregated once per user (not per word) during the batch read phase and
 * carried through the pipeline as part of {@link UserWordPair}. This avoids
 * repeated DB lookups for the same user across multiple words in one chunk.
 *
 * @param proficiencyLevel  CEFR level string (e.g. "A1", "B2", "C1"); may be
 *                          {@code null} if the user hasn't set a level yet
 * @param learningGoalNames Active learning goal display names
 *                          (e.g. "Business English", "IELTS Preparation")
 * @param learnedWordCount  Total words the user has started learning
 *                          (any {@code user_vocab_progress} row exists)
 * @param masteredWordCount Total words the user has fully mastered
 *                          ({@code is_mastered = true})
 */
public record UserContext(
        String proficiencyLevel,
        List<String> learningGoalNames,
        int learnedWordCount,
        int masteredWordCount) {

    /** A fallback context used when user data cannot be loaded. */
    public static UserContext unknown() {
        return new UserContext("unknown", List.of(), 0, 0);
    }
}
