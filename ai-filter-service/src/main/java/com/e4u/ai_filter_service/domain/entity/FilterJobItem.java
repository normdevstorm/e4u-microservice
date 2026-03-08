package com.e4u.ai_filter_service.domain.entity;

import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores the per-user, per-word AI relevance scoring result for a given Spring
 * Batch job execution.
 *
 * <p>
 * Each row represents: "for user X, word Y has relevance tier Z at this point
 * in time."
 * The score is personalized — the same word can have different tiers for
 * different users
 * depending on their proficiency, goals, and progress.
 *
 * <p>
 * Managed by: {@code aiFilterEntityManager} → {@code e4u_ai_filter} DB.
 */
@Entity
@Table(name = "filter_job_items", indexes = {
        @Index(name = "idx_fji_job_execution_id", columnList = "job_execution_id"),
        @Index(name = "idx_fji_user_id", columnList = "user_id"),
        @Index(name = "idx_fji_word_id", columnList = "word_id"),
        @Index(name = "idx_fji_relevance_tier", columnList = "relevance_tier"),
        @Index(name = "idx_fji_user_word", columnList = "user_id, word_id"),
        @Index(name = "idx_fji_word_context_template", columnList = "word_context_template_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterJobItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * References {@code BATCH_JOB_EXECUTION.JOB_EXECUTION_ID}.
     * Used to correlate each result back to the batch run that produced it.
     */
    @Column(name = "job_execution_id", nullable = false)
    private Long jobExecutionId;

    /**
     * The user this relevance score was computed for.
     * Cross-service reference — no JPA FK to account/auth service.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * UUID of the word in {@code e4u_learning.global_dictionary}.
     * Cross-DB reference maintained by application logic.
     */
    @Column(name = "word_id", nullable = false)
    private UUID wordId;

    /**
     * The base form (lemma) of the word at the time of processing. Denormalized for
     * audit.
     */
    @Column(name = "word_lemma", nullable = false, length = 100)
    private String wordLemma;

    /** AI-assigned relevance tier for this user-word pair. */
    @Enumerated(EnumType.STRING)
    @Column(name = "relevance_tier", nullable = false, length = 20)
    private WordRelevanceTier relevanceTier;

    /**
     * Continuous relevance score from the AI model (0.0 – 1.0).
     * Complements the tier: two HIGH words can differ in score (e.g. 0.95 vs 0.72).
     */
    @Column(name = "relevance_score")
    private Float relevanceScore;

    /**
     * AI reasoning for the assigned tier (e.g. "Matches B1 level + business goal").
     */
    @Column(name = "ai_reason", columnDefinition = "TEXT")
    private String aiReason;

    /** Timestamp when this user-word pair was processed by the batch step. */
    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    /**
     * UUID of the {@code word_context_templates} row that triggered this scoring.
     * <p>
     * The writer uses this to call back into {@code e4u_learning}:
     * <ul>
     * <li>HIGH / MEDIUM → {@code is_selected_by_ai = true} (word enters
     * curriculum)</li>
     * <li>LOW → {@code is_selected_by_ai} stays false (word rejected)</li>
     * </ul>
     * Always set when the source is a user context template (nullable only for
     * legacy rows).
     */
    @Column(name = "word_context_template_id")
    private UUID wordContextTemplateId;
}
