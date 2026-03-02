package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity tracking user's vocabulary learning progress.
 * Implements Spaced Repetition System (SRS) algorithm fields.
 * Replaces the old UserVocabInstance entity with cleaner separation of concerns.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_vocab_progress", uniqueConstraints = {
    @UniqueConstraint(name = "unique_user_word_progress", columnNames = {"user_id", "word_id"})
})
public class UserVocabProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private GlobalDictionary word;

    /**
     * The currently active context for this word-user pair.
     * Determines which contextual example the user is learning with.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_context_id")
    private WordContextTemplate activeContext;

    @Column(name = "is_mastered")
    @Builder.Default
    private Boolean isMastered = false;

    /**
     * How relevant this word is to the user's learning goals (0.0 - 1.0)
     */
    @Column(name = "relevance_score")
    private Float relevanceScore;

    // ========== Spaced Repetition System (SRS) Fields ==========

    /**
     * Current interval in days before next review
     */
    @Column(name = "interval_days")
    @Builder.Default
    private Integer intervalDays = 0;

    /**
     * Ease factor for SRS algorithm (default 2.5, range typically 1.3-2.5)
     * Higher = word is easier, longer intervals
     * Lower = word is harder, shorter intervals
     */
    @Column(name = "ease_factor")
    @Builder.Default
    private Float easeFactor = 2.5f;

    /**
     * Streak of consecutive correct answers
     */
    @Column(name = "consecutive_correct_answers")
    @Builder.Default
    private Integer consecutiveCorrectAnswers = 0;

    /**
     * When this word should next be reviewed
     */
    @Column(name = "next_review_at")
    @Builder.Default
    private LocalDateTime nextReviewAt = LocalDateTime.now();

    // ========== SRS Algorithm Methods ==========

    /**
     * Record a correct answer and update SRS parameters
     * Uses SM-2 algorithm variant
     */
    public void recordCorrectAnswer() {
        this.consecutiveCorrectAnswers++;
        
        // Increase ease factor slightly for correct answers
        this.easeFactor = Math.min(2.5f, this.easeFactor + 0.1f);
        
        // Calculate next interval
        if (this.consecutiveCorrectAnswers == 1) {
            this.intervalDays = 1;
        } else if (this.consecutiveCorrectAnswers == 2) {
            this.intervalDays = 6;
        } else {
            this.intervalDays = Math.round(this.intervalDays * this.easeFactor);
        }
        
        // Set next review date
        this.nextReviewAt = LocalDateTime.now().plusDays(this.intervalDays);
        
        // Mark as mastered if interval exceeds 21 days
        if (this.intervalDays >= 21) {
            this.isMastered = true;
        }
    }

    /**
     * Record an incorrect answer and reset SRS parameters
     */
    public void recordIncorrectAnswer() {
        this.consecutiveCorrectAnswers = 0;
        
        // Decrease ease factor for incorrect answers
        this.easeFactor = Math.max(1.3f, this.easeFactor - 0.2f);
        
        // Reset interval
        this.intervalDays = 0;
        
        // Review again soon (in 10 minutes effectively, but we use 0 days)
        this.nextReviewAt = LocalDateTime.now();
        
        // No longer mastered
        this.isMastered = false;
    }

    /**
     * Check if this word is due for review
     */
    public boolean isDueForReview() {
        return !this.isMastered && LocalDateTime.now().isAfter(this.nextReviewAt);
    }
}
