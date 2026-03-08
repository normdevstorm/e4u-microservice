package com.e4u.ai_filter_service.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/**
 * Read-only JPA projection of {@code user_vocab_progress} in
 * {@code e4u_learning}.
 *
 * <p>
 * This entity is managed by the {@code learningEntityManager} /
 * {@code learningDataSource}.
 * It is marked {@link Immutable} to prevent accidental writes through JPA.
 *
 * <p>
 * Purpose in the ai-filter-service:
 * <ul>
 * <li><strong>Reader</strong>: detect unscored (user, word) pairs
 * ({@code relevance_score IS NULL})</li>
 * <li><strong>Processor</strong>: look up the user's mastery stats for building
 * the AI relevance request</li>
 * <li><strong>Writer</strong>: the {@code UserVocabProgressReadRepository}
 * performs
 * the write-back update on {@code relevance_score} via a native
 * {@code @Modifying}
 * query — NOT through this entity</li>
 * </ul>
 */
@Entity
@Immutable
@Getter
@NoArgsConstructor
@Table(name = "user_vocab_progress")
public class UserVocabProgressReadOnly {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** UUID of the learner who owns this progress record. */
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    /** UUID FK to {@code global_dictionary.id}. */
    @Column(name = "word_id", updatable = false, nullable = false)
    private UUID wordId;

    /**
     * Whether the user has mastered this word (interval ≥ 21 days in SRS).
     * A mastered word should typically receive a LOW relevance tier.
     */
    @Column(name = "is_mastered", updatable = false)
    private Boolean isMastered;

    /**
     * AI-assigned relevance score (0.0 – 1.0); {@code NULL} means not yet scored.
     * The batch job processes all records where this is NULL.
     */
    @Column(name = "relevance_score", updatable = false)
    private Float relevanceScore;

    /**
     * Number of days until next SRS review.
     * High interval + mastered = low relevance for re-scoring.
     */
    @Column(name = "interval_days", updatable = false)
    private Integer intervalDays;

    /** Consecutive correct answers streak (SRS). */
    @Column(name = "consecutive_correct_answers", updatable = false)
    private Integer consecutiveCorrectAnswers;
}
