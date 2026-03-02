package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.request.UserVocabProgressCreateRequest;
import com.e4u.learning_service.dtos.response.UserVocabProgressResponse;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.UserVocabProgress;
import com.e4u.learning_service.entities.WordContextTemplate;
import com.e4u.learning_service.mapper.UserVocabProgressMapper;
import com.e4u.learning_service.repositories.GlobalDictionaryRepository;
import com.e4u.learning_service.repositories.UserVocabProgressRepository;
import com.e4u.learning_service.repositories.WordContextTemplateRepository;
import com.e4u.learning_service.services.UserVocabProgressService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserVocabProgressService.
 * Implements Spaced Repetition System (SRS) logic for vocabulary learning.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserVocabProgressServiceImpl implements UserVocabProgressService {

    private final UserVocabProgressRepository progressRepository;
    private final GlobalDictionaryRepository dictionaryRepository;
    private final WordContextTemplateRepository contextRepository;
    private final UserVocabProgressMapper progressMapper;

    @Override
    @Transactional
    public UserVocabProgressResponse createOrGetProgress(UserVocabProgressCreateRequest request) {
        log.info("Creating or getting vocab progress for user: {} word: {}", 
                request.getUserId(), request.getWordId());

        Optional<UserVocabProgress> existing = progressRepository
                .findByUserIdAndWordId(request.getUserId(), request.getWordId());

        if (existing.isPresent()) {
            return progressMapper.toResponse(existing.get());
        }

        GlobalDictionary word = dictionaryRepository.findById(request.getWordId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Word not found with ID: " + request.getWordId()));

        WordContextTemplate context = null;
        if (request.getActiveContextId() != null) {
            context = contextRepository.findById(request.getActiveContextId()).orElse(null);
        }

        UserVocabProgress progress = UserVocabProgress.builder()
                .userId(request.getUserId())
                .word(word)
                .activeContext(context)
                .relevanceScore(request.getRelevanceScore())
                .isMastered(false)
                .intervalDays(0)
                .easeFactor(2.5f)
                .consecutiveCorrectAnswers(0)
                .nextReviewAt(LocalDateTime.now())
                .build();

        progress = progressRepository.save(progress);
        log.info("Created vocab progress with ID: {}", progress.getId());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVocabProgressResponse getProgressById(UUID id) {
        log.debug("Fetching vocab progress by ID: {}", id);

        UserVocabProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Progress not found with ID: " + id));

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVocabProgressResponse getProgressByUserAndWord(UUID userId, UUID wordId) {
        log.debug("Fetching vocab progress for user: {} word: {}", userId, wordId);

        UserVocabProgress progress = progressRepository.findByUserIdAndWordId(userId, wordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Progress not found for user: " + userId + " word: " + wordId));

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabProgressResponse> getProgressByUser(UUID userId) {
        log.debug("Fetching all vocab progress for user: {}", userId);

        List<UserVocabProgress> progressList = progressRepository.findByUserId(userId);
        return progressMapper.toResponseList(progressList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabProgressResponse> getWordsDueForReview(UUID userId) {
        log.debug("Fetching words due for review for user: {}", userId);

        List<UserVocabProgress> dueWords = progressRepository
                .findDueForReview(userId, LocalDateTime.now());
        return progressMapper.toResponseList(dueWords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabProgressResponse> getWordsDueForReview(UUID userId, int limit) {
        log.debug("Fetching top {} words due for review for user: {}", limit, userId);

        List<UserVocabProgress> dueWords = progressRepository
                .findTopDueForReview(userId, LocalDateTime.now(), PageRequest.of(0, limit));
        return progressMapper.toResponseList(dueWords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabProgressResponse> getMasteredWords(UUID userId) {
        log.debug("Fetching mastered words for user: {}", userId);

        List<UserVocabProgress> mastered = progressRepository.findByUserIdAndIsMasteredTrue(userId);
        return progressMapper.toResponseList(mastered);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabProgressResponse> getWordsInLearning(UUID userId) {
        log.debug("Fetching words in learning for user: {}", userId);

        List<UserVocabProgress> learning = progressRepository.findByUserIdAndIsMasteredFalse(userId);
        return progressMapper.toResponseList(learning);
    }

    @Override
    @Transactional
    public UserVocabProgressResponse recordCorrectAnswer(UUID userId, UUID wordId) {
        log.info("Recording correct answer for user: {} word: {}", userId, wordId);

        UserVocabProgress progress = getOrCreateProgress(userId, wordId);
        progress.recordCorrectAnswer();
        progress = progressRepository.save(progress);

        log.debug("Updated progress - interval: {}d, next review: {}, mastered: {}", 
                progress.getIntervalDays(), progress.getNextReviewAt(), progress.getIsMastered());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public UserVocabProgressResponse recordIncorrectAnswer(UUID userId, UUID wordId) {
        log.info("Recording incorrect answer for user: {} word: {}", userId, wordId);

        UserVocabProgress progress = getOrCreateProgress(userId, wordId);
        progress.recordIncorrectAnswer();
        progress = progressRepository.save(progress);

        log.debug("Updated progress - interval: {}d, next review: {}", 
                progress.getIntervalDays(), progress.getNextReviewAt());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public UserVocabProgressResponse updateRelevanceScore(UUID userId, UUID wordId, float relevanceScore) {
        log.info("Updating relevance score for user: {} word: {} to: {}", userId, wordId, relevanceScore);

        UserVocabProgress progress = progressRepository.findByUserIdAndWordId(userId, wordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Progress not found for user: " + userId + " word: " + wordId));

        progress.setRelevanceScore(relevanceScore);
        progress = progressRepository.save(progress);

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public UserVocabProgressResponse updateActiveContext(UUID userId, UUID wordId, UUID contextId) {
        log.info("Updating active context for user: {} word: {} to context: {}", userId, wordId, contextId);

        UserVocabProgress progress = progressRepository.findByUserIdAndWordId(userId, wordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Progress not found for user: " + userId + " word: " + wordId));

        WordContextTemplate context = contextRepository.findById(contextId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Context not found with ID: " + contextId));

        progress.setActiveContext(context);
        progress = progressRepository.save(progress);

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public List<UserVocabProgressResponse> initializeProgressForWords(UUID userId, List<UUID> wordIds) {
        log.info("Initializing progress for user: {} with {} words", userId, wordIds.size());

        List<UserVocabProgress> newProgress = new ArrayList<>();

        for (UUID wordId : wordIds) {
            // Skip if already exists
            Optional<UserVocabProgress> existing = progressRepository.findByUserIdAndWordId(userId, wordId);
            if (existing.isPresent()) {
                continue;
            }

            GlobalDictionary word = dictionaryRepository.findById(wordId).orElse(null);
            if (word == null) {
                log.warn("Word not found with ID: {} - skipping", wordId);
                continue;
            }

            UserVocabProgress progress = UserVocabProgress.builder()
                    .userId(userId)
                    .word(word)
                    .isMastered(false)
                    .intervalDays(0)
                    .easeFactor(2.5f)
                    .consecutiveCorrectAnswers(0)
                    .nextReviewAt(LocalDateTime.now())
                    .build();

            newProgress.add(progress);
        }

        if (!newProgress.isEmpty()) {
            newProgress = progressRepository.saveAll(newProgress);
            log.info("Initialized {} new vocab progress records", newProgress.size());
        }

        return progressMapper.toResponseList(newProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMasteredWords(UUID userId) {
        return progressRepository.countByUserIdAndIsMasteredTrue(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countWordsInLearning(UUID userId) {
        return progressRepository.countByUserIdAndIsMasteredFalse(userId);
    }

    @Override
    @Transactional
    public void deleteProgress(UUID id) {
        log.info("Deleting vocab progress: {}", id);

        if (!progressRepository.existsById(id)) {
            throw new EntityNotFoundException("Progress not found with ID: " + id);
        }

        progressRepository.deleteById(id);
        log.info("Deleted vocab progress: {}", id);
    }

    // ========== Private Helper Methods ==========

    private UserVocabProgress getOrCreateProgress(UUID userId, UUID wordId) {
        return progressRepository.findByUserIdAndWordId(userId, wordId)
                .orElseGet(() -> {
                    GlobalDictionary word = dictionaryRepository.findById(wordId)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Word not found with ID: " + wordId));

                    return UserVocabProgress.builder()
                            .userId(userId)
                            .word(word)
                            .isMastered(false)
                            .intervalDays(0)
                            .easeFactor(2.5f)
                            .consecutiveCorrectAnswers(0)
                            .nextReviewAt(LocalDateTime.now())
                            .build();
                });
    }
}
