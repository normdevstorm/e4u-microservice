package com.e4u.ai_filter_service.batch.domain;

import com.e4u.ai_filter_service.learning.entity.GlobalDictionaryReadOnly;

import java.util.UUID;

/**
 * Immutable carrier for a single (user, word-context-template) batch item,
 * flowing from {@code GlobalDictionaryItemReader} through
 * {@code WordFilterItemProcessor}.
 *
 * <p>
 * Built from a JOIN of {@code word_context_templates wct}
 * and {@code global_dictionary gd} where
 * {@code wct.created_for_user_id IS NOT NULL AND wct.ai_reasoning IS NULL}.
 *
 * @param contextTemplateId   UUID of the {@code word_context_templates} row
 *                            being
 *                            evaluated. The writer uses this to call back into
 *                            {@code e4u_learning} and flip
 *                            {@code is_selected_by_ai}.
 * @param userId              The user this context template was generated for.
 * @param wordId              UUID of the word in {@code global_dictionary}.
 * @param word                Projection of the {@code global_dictionary} row.
 * @param userContextSentence User-specific example sentence from
 *                            {@code word_context_templates.context_sentence}.
 *                            May be {@code null} if the template has none.
 *                            Passed to the AI for richer relevance assessment.
 * @param userContext         Pre-fetched aggregated learner context for the AI
 *                            prompt.
 */
public record UserWordPair(
        UUID contextTemplateId,
        UUID userId,
        UUID wordId,
        GlobalDictionaryReadOnly word,
        String userContextSentence,
        UserContext userContext) {
}
