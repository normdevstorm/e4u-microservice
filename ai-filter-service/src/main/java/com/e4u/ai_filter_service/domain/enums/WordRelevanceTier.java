package com.e4u.ai_filter_service.domain.enums;

/**
 * AI-assigned relevance tier for a word relative to a specific user's learning profile.
 *
 * <p>Determined by considering the user's proficiency level, active learning goals,
 * vocabulary already mastered, and contextual utility of the word.
 */
public enum WordRelevanceTier {

    /**
     * Word is highly relevant to this user right now.
     * Matches their current proficiency level, active goals, and is not yet mastered.
     * Should be prioritized in lesson generation and vocabulary recommendations.
     */
    HIGH,

    /**
     * Word has moderate relevance — useful but not immediately critical.
     * May be slightly above/below current level or tangentially related to goals.
     */
    MEDIUM,

    /**
     * Word has low relevance for this user at this time.
     * Already mastered, too advanced, too basic, or unrelated to any active goal.
     */
    LOW
}
