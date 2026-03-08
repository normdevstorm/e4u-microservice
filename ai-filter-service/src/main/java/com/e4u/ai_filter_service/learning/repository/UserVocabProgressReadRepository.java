package com.e4u.ai_filter_service.learning.repository;

import com.e4u.ai_filter_service.domain.enums.WordRelevanceTier;
import com.e4u.ai_filter_service.learning.entity.UserVocabProgressReadOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link UserVocabProgressReadOnly} on {@code e4u_learning}.
 *
 * <p>
 * Backed by {@code learningEntityManager} / {@code learningTransactionManager}
 * (configured in {@code LearningDataSourceConfig}).
 *
 * <p>
 * Read operations detect which (user, word) pairs still need an AI relevance
 * score. The single write operation ({@link #writeBackRelevanceScore}) updates
 * {@code user_vocab_progress.relevance_score} as a best-effort denormalization
 * after each batch chunk.
 */
@Repository
public interface UserVocabProgressReadRepository extends JpaRepository<UserVocabProgressReadOnly, UUID> {

    /**
     * Find all (user, word) pairs that have not yet received an AI relevance
     * score ({@code relevance_score IS NULL}).
     *
     * <p>
     * This is the primary feed for the batch reader. Pairs where the score is
     * already set are skipped; they will only be re-scored if the score is
     * explicitly reset (e.g. by a forced re-run endpoint).
     */
    @Query("SELECT uvp FROM UserVocabProgressReadOnly uvp WHERE uvp.relevanceScore IS NULL")
    List<UserVocabProgressReadOnly> findUnscoredPairs();

    /**
     * Count all (user, word) pairs still awaiting an AI relevance score.
     * Used by the management API ({@code /pending-count} endpoint).
     */
    @Query("SELECT COUNT(uvp) FROM UserVocabProgressReadOnly uvp WHERE uvp.relevanceScore IS NULL")
    long countUnscoredPairs();

    /**
     * Count how many words a user has started learning (any vocab progress record
     * exists, mastered or not). Used to populate {@code learnedWordCount} in the AI
     * request.
     */
    @Query("SELECT COUNT(uvp) FROM UserVocabProgressReadOnly uvp WHERE uvp.userId = :userId")
    long countLearnedWordsByUser(@Param("userId") UUID userId);

    /**
     * Count how many words a user has fully mastered ({@code is_mastered = true}).
     * Used to populate {@code masteredWordCount} in the AI request.
     */
    @Query("SELECT COUNT(uvp) FROM UserVocabProgressReadOnly uvp WHERE uvp.userId = :userId AND uvp.isMastered = true")
    long countMasteredWordsByUser(@Param("userId") UUID userId);

    // ─── Write-back ──────────────────────────────────────────────────────────

    /**
     * Best-effort write-back: update {@code user_vocab_progress.relevance_score}
     * for a specific (user, word) pair after the AI has scored it.
     *
     * <p>
     * <strong>This is the ONLY write operation permitted on {@code e4u_learning}
     * from this service.</strong> It runs in its own transaction on
     * {@code learningTransactionManager}.
     *
     * <p>
     * The {@code relevanceTier} is stored as a string comment in the reason column;
     * the actual tier is persisted in {@code filter_job_items} in the
     * {@code e4u_ai_filter} DB (source of truth). Here we only update the score
     * float so that learning-service can use it for ranking/sorting without calling
     * ai-filter-service.
     *
     * @param userId         UUID of the learner
     * @param wordId         UUID of the word in {@code global_dictionary}
     * @param relevanceScore Continuous score from the AI (0.0 – 1.0)
     */
    @Modifying
    @Transactional(transactionManager = "learningTransactionManager")
    @Query(value = "UPDATE user_vocab_progress SET relevance_score = :relevanceScore " +
            "WHERE user_id = :userId AND word_id = :wordId", nativeQuery = true)
    void writeBackRelevanceScore(
            @Param("userId") UUID userId,
            @Param("wordId") UUID wordId,
            @Param("relevanceScore") Float relevanceScore);
}
