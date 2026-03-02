package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.UserVocabProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserVocabProgress entity.
 * Provides access to user's vocabulary learning progress with SRS support.
 */
@Repository
public interface UserVocabProgressRepository extends JpaRepository<UserVocabProgress, UUID> {

    /**
     * Find progress record for a specific user-word pair
     */
    Optional<UserVocabProgress> findByUserIdAndWordId(UUID userId, UUID wordId);

    /**
     * Find all progress records for a user
     */
    List<UserVocabProgress> findByUserId(UUID userId);

    /**
     * Find all mastered words for a user
     */
    List<UserVocabProgress> findByUserIdAndIsMasteredTrue(UUID userId);

    /**
     * Find words due for review (SRS)
     */
    @Query("SELECT uvp FROM UserVocabProgress uvp " +
           "WHERE uvp.userId = :userId " +
           "AND uvp.isMastered = false " +
           "AND uvp.nextReviewAt <= :now " +
           "ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabProgress> findDueForReview(
        @Param("userId") UUID userId, 
        @Param("now") LocalDateTime now
    );

    /**
     * Find top N words due for review
     */
    @Query("SELECT uvp FROM UserVocabProgress uvp " +
           "WHERE uvp.userId = :userId " +
           "AND uvp.isMastered = false " +
           "AND uvp.nextReviewAt <= :now " +
           "ORDER BY uvp.nextReviewAt ASC " +
           "LIMIT :limit")
    List<UserVocabProgress> findTopDueForReview(
        @Param("userId") UUID userId, 
        @Param("now") LocalDateTime now,
        @Param("limit") int limit
    );

    /**
     * Find progress with word and context eagerly loaded
     */
    @Query("SELECT uvp FROM UserVocabProgress uvp " +
           "LEFT JOIN FETCH uvp.word " +
           "LEFT JOIN FETCH uvp.activeContext " +
           "WHERE uvp.userId = :userId AND uvp.id = :id")
    Optional<UserVocabProgress> findByIdWithDetails(
        @Param("userId") UUID userId, 
        @Param("id") UUID id
    );

    /**
     * Count mastered words for a user
     */
    long countByUserIdAndIsMasteredTrue(UUID userId);

    /**
     * Count words not mastered for a user
     */
    long countByUserIdAndIsMasteredFalse(UUID userId);

    /**
     * Find words not mastered for a user
     */
    List<UserVocabProgress> findByUserIdAndIsMasteredFalse(UUID userId);

    /**
     * Find top N words due for review with Pageable
     */
    @Query("SELECT uvp FROM UserVocabProgress uvp " +
           "WHERE uvp.userId = :userId " +
           "AND uvp.isMastered = false " +
           "AND uvp.nextReviewAt <= :now " +
           "ORDER BY uvp.nextReviewAt ASC")
    List<UserVocabProgress> findTopDueForReview(
        @Param("userId") UUID userId, 
        @Param("now") LocalDateTime now,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Count total words in progress for a user
     */
    long countByUserId(UUID userId);

    /**
     * Find by relevance score threshold
     */
    @Query("SELECT uvp FROM UserVocabProgress uvp " +
           "WHERE uvp.userId = :userId " +
           "AND uvp.relevanceScore >= :minScore " +
           "ORDER BY uvp.relevanceScore DESC")
    List<UserVocabProgress> findByRelevanceScore(
        @Param("userId") UUID userId, 
        @Param("minScore") Float minScore
    );

    /**
     * Check if progress exists for user-word pair
     */
    boolean existsByUserIdAndWordId(UUID userId, UUID wordId);

    /**
     * Delete all progress for a user (for data cleanup)
     */
    void deleteByUserId(UUID userId);

    /**
     * Find words with low ease factor (struggling words)
     */
    @Query("SELECT uvp FROM UserVocabProgress uvp " +
           "WHERE uvp.userId = :userId " +
           "AND uvp.easeFactor < :threshold " +
           "ORDER BY uvp.easeFactor ASC")
    List<UserVocabProgress> findStrugglingWords(
        @Param("userId") UUID userId, 
        @Param("threshold") Float threshold
    );
}
