package com.e4u.ai_filter_service.client.dto;

import java.util.List;
import java.util.UUID;

/**
 * Request payload sent to the AI relevance-scoring API for a single
 * user-word pair.
 *
 * <p>
 * The word fields describe the vocabulary item from {@code global_dictionary}.
 * The user context fields describe the learner's current profile so the AI can
 * determine whether this word is relevant <em>right now</em> for this specific
 * user.
 *
 * @param wordId            UUID of the word in {@code global_dictionary}
 * @param lemma             Base form of the word
 * @param partOfSpeech      Grammatical category (noun, verb, adjective, etc.)
 * @param definition        English definition of the word
 * @param exampleSentence   A contextual usage example
 * @param userId            UUID of the learner being scored
 * @param proficiencyLevel  CEFR level string (e.g. "A1", "B2", "C1")
 * @param learningGoals     List of active goal names (e.g. ["Business English",
 *                          "IELTS"])
 * @param learnedWordCount  Total words the user has started learning (not yet
 *                          mastered)
 * @param masteredWordCount Total words the user has fully mastered
 */
public record AiFilterRequest(
        // ── Word fields ──────────────────────────────────────────────────────
        UUID wordId,
        String lemma,
        String partOfSpeech,
        String definition,
        String exampleSentence,
        /**
         * User-specific example sentence from
         * {@code word_context_templates.context_sentence}.
         * When non-null, the AI uses this over the generic {@code exampleSentence} —
         * it shows the word in the exact context the learner will encounter it.
         */
        String userContextSentence,
        // ── User context fields ───────────────────────────────────────────────
        UUID userId,
        String proficiencyLevel,
        List<String> learningGoals,
        int learnedWordCount,
        int masteredWordCount) {
}
