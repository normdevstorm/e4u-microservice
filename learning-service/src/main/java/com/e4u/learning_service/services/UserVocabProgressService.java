package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.UserVocabProgressCreateRequest;
import com.e4u.learning_service.dtos.response.UserVocabProgressResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing user vocabulary progress.
 * Implements Spaced Repetition System (SRS) logic.
 */
public interface UserVocabProgressService {

    /**
     * Create or get user vocab progress for a word
     */
    UserVocabProgressResponse createOrGetProgress(UserVocabProgressCreateRequest request);

    /**
     * Get progress by ID
     */
    UserVocabProgressResponse getProgressById(UUID id);

    /**
     * Get progress for a specific user and word
     */
    UserVocabProgressResponse getProgressByUserAndWord(UUID userId, UUID wordId);

    /**
     * Get all vocab progress for a user
     */
    List<UserVocabProgressResponse> getProgressByUser(UUID userId);

    /**
     * Get words due for review (based on SRS scheduling)
     */
    List<UserVocabProgressResponse> getWordsDueForReview(UUID userId);

    /**
     * Get words due for review with limit
     */
    List<UserVocabProgressResponse> getWordsDueForReview(UUID userId, int limit);

    /**
     * Get mastered words for a user
     */
    List<UserVocabProgressResponse> getMasteredWords(UUID userId);

    /**
     * Get words in learning (not mastered) for a user
     */
    List<UserVocabProgressResponse> getWordsInLearning(UUID userId);

    /**
     * Record a correct answer for SRS update
     */
    UserVocabProgressResponse recordCorrectAnswer(UUID userId, UUID wordId);

    /**
     * Record an incorrect answer for SRS update
     */
    UserVocabProgressResponse recordIncorrectAnswer(UUID userId, UUID wordId);

    /**
     * Update relevance score for a word
     */
    UserVocabProgressResponse updateRelevanceScore(UUID userId, UUID wordId, float relevanceScore);

    /**
     * Update active context for a word
     */
    UserVocabProgressResponse updateActiveContext(UUID userId, UUID wordId, UUID contextId);

    /**
     * Batch initialize progress for multiple words
     */
    List<UserVocabProgressResponse> initializeProgressForWords(UUID userId, List<UUID> wordIds);

    /**
     * Get count of words by mastery status
     */
    long countMasteredWords(UUID userId);

    /**
     * Get count of words in learning
     */
    long countWordsInLearning(UUID userId);

    /**
     * Delete progress record
     */
    void deleteProgress(UUID id);
}
