package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.request.UserLessonSessionStartRequest;
import com.e4u.learning_service.dtos.response.ExerciseTemplateResponse;
import com.e4u.learning_service.dtos.response.UserLessonSessionDetailResponse;
import com.e4u.learning_service.dtos.response.UserLessonSessionResponse;
import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.LessonTemplate;
import com.e4u.learning_service.entities.UserLessonSession;
import com.e4u.learning_service.entities.UserLessonSession.SessionStatus;
import com.e4u.learning_service.entities.UserUnitState;
import com.e4u.learning_service.mapper.ExerciseTemplateMapper;
import com.e4u.learning_service.mapper.UserLessonSessionMapper;
import com.e4u.learning_service.repositories.ExerciseTemplateRepository;
import com.e4u.learning_service.repositories.LessonTemplateRepository;
import com.e4u.learning_service.repositories.UserLessonSessionRepository;
import com.e4u.learning_service.repositories.UserUnitStateRepository;
import com.e4u.learning_service.services.UserLessonSessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserLessonSessionService.
 * Manages user lesson session lifecycle: start, progress, pause, complete.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLessonSessionServiceImpl implements UserLessonSessionService {

    private final UserLessonSessionRepository sessionRepository;
    private final LessonTemplateRepository lessonTemplateRepository;
    private final UserUnitStateRepository userUnitStateRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final UserLessonSessionMapper sessionMapper;
    private final ExerciseTemplateMapper exerciseTemplateMapper;

    @Override
    @Transactional
    public UserLessonSessionDetailResponse startOrResumeSession(UserLessonSessionStartRequest request) {
        log.info("Starting or resuming session for user: {} on lesson: {}", 
                request.getUserId(), request.getLessonTemplateId());

        // Check for existing session
        Optional<UserLessonSession> existingSession = sessionRepository
                .findByUserIdAndLessonTemplateId(request.getUserId(), request.getLessonTemplateId());

        UserLessonSession session;
        if (existingSession.isPresent()) {
            session = existingSession.get();
            if (session.getStatus() == SessionStatus.COMPLETED) {
                // Reset completed session for re-learning
                log.info("Resetting completed session for re-learning: {}", session.getId());
                session.setStatus(SessionStatus.NOT_STARTED);
                session.setCompletedItems(0);
                session.setCorrectItems(0);
                session.setAccuracyRate(null);
            } else {
                log.info("Resuming existing session: {}", session.getId());
            }
        } else {
            // Create new session
            LessonTemplate lessonTemplate = lessonTemplateRepository.findById(request.getLessonTemplateId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "LessonTemplate not found with ID: " + request.getLessonTemplateId()));

            UserUnitState userUnitState = null;
            if (request.getUserUnitStateId() != null) {
                userUnitState = userUnitStateRepository.findById(request.getUserUnitStateId()).orElse(null);
            }

            // Get exercise count for this lesson
            List<ExerciseTemplate> exercises = exerciseTemplateRepository
                    .findByLessonTemplateIdForUser(request.getLessonTemplateId(), request.getUserId());

            session = UserLessonSession.builder()
                    .userId(request.getUserId())
                    .lessonTemplate(lessonTemplate)
                    .userUnitState(userUnitState)
                    .status(SessionStatus.NOT_STARTED)
                    .totalItems(exercises.size())
                    .completedItems(0)
                    .correctItems(0)
                    .build();

            session = sessionRepository.save(session);
            log.info("Created new session with ID: {}", session.getId());
        }

        // Start the session if not started
        if (session.getStatus() == SessionStatus.NOT_STARTED) {
            List<ExerciseTemplate> exercises = exerciseTemplateRepository
                    .findByLessonTemplateIdForUser(request.getLessonTemplateId(), request.getUserId());
            session.start(exercises.size());
            session = sessionRepository.save(session);
        }

        // Build detailed response with exercises
        return buildDetailedResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public UserLessonSessionResponse getSessionById(UUID sessionId) {
        log.debug("Fetching session by ID: {}", sessionId);

        UserLessonSession session = sessionRepository.findByIdWithLessonTemplate(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public UserLessonSessionDetailResponse getSessionDetail(UUID sessionId) {
        log.debug("Fetching session detail by ID: {}", sessionId);

        UserLessonSession session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        return buildDetailedResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLessonSessionResponse> getSessionsByUser(UUID userId) {
        log.debug("Fetching sessions for user: {}", userId);

        List<UserLessonSession> sessions = sessionRepository.findByUserId(userId);
        return sessionMapper.toResponseList(sessions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLessonSessionResponse> getSessionsByUserAndUnit(UUID userId, UUID userUnitStateId) {
        log.debug("Fetching sessions for user: {} in unit state: {}", userId, userUnitStateId);

        List<UserLessonSession> sessions = sessionRepository.findByUserUnitStateId(userUnitStateId);
        return sessionMapper.toResponseList(sessions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLessonSessionResponse> getSessionsByUserAndStatus(UUID userId, SessionStatus status) {
        log.debug("Fetching sessions for user: {} with status: {}", userId, status);

        List<UserLessonSession> sessions = sessionRepository.findByUserIdAndStatus(userId, status);
        return sessionMapper.toResponseList(sessions);
    }

    @Override
    @Transactional
    public UserLessonSessionResponse pauseSession(UUID sessionId) {
        log.info("Pausing session: {}", sessionId);

        UserLessonSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        if (session.getStatus() == SessionStatus.IN_PROGRESS) {
            session.pause();
            session = sessionRepository.save(session);
            log.info("Session paused: {}", sessionId);
        }

        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public UserLessonSessionResponse completeSession(UUID sessionId) {
        log.info("Completing session: {}", sessionId);

        UserLessonSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        session.complete();
        session = sessionRepository.save(session);

        // TODO: Update UserUnitState progress if linked
        if (session.getUserUnitState() != null) {
            log.info("Updating unit state progress for session: {}", sessionId);
            // Trigger unit state recalculation
        }

        log.info("Session completed: {} with accuracy: {}", sessionId, session.getAccuracyRate());
        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public UserLessonSessionResponse updateSessionProgress(UUID sessionId, boolean isCorrect) {
        log.debug("Updating session progress: {} isCorrect: {}", sessionId, isCorrect);

        UserLessonSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        session.recordExerciseCompletion(isCorrect);

        // Auto-complete if all exercises done
        if (session.getCompletedItems() != null && session.getTotalItems() != null 
                && session.getCompletedItems() >= session.getTotalItems()) {
            session.complete();
        }

        session = sessionRepository.save(session);
        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional
    public UserLessonSessionResponse getOrCreateSession(UUID userId, UUID lessonTemplateId, UUID userUnitStateId) {
        log.debug("Getting or creating session for user: {} on lesson: {}", userId, lessonTemplateId);

        Optional<UserLessonSession> existingSession = sessionRepository
                .findByUserIdAndLessonTemplateId(userId, lessonTemplateId);

        if (existingSession.isPresent()) {
            return sessionMapper.toResponse(existingSession.get());
        }

        // Create new session
        UserLessonSessionStartRequest request = UserLessonSessionStartRequest.builder()
                .userId(userId)
                .lessonTemplateId(lessonTemplateId)
                .userUnitStateId(userUnitStateId)
                .build();

        UserLessonSessionDetailResponse detail = startOrResumeSession(request);
        
        // Convert to simple response
        return UserLessonSessionResponse.builder()
                .id(detail.getId())
                .userId(detail.getUserId())
                .lessonTemplateId(detail.getLessonTemplateId())
                .lessonName(detail.getLessonName())
                .userUnitStateId(detail.getUserUnitStateId())
                .status(detail.getStatus())
                .totalItems(detail.getTotalItems())
                .completedItems(detail.getCompletedItems())
                .correctItems(detail.getCorrectItems())
                .accuracyRate(detail.getAccuracyRate())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteSession(UUID sessionId) {
        log.info("Deleting session: {}", sessionId);

        if (!sessionRepository.existsById(sessionId)) {
            throw new EntityNotFoundException("Session not found with ID: " + sessionId);
        }

        sessionRepository.deleteById(sessionId);
        log.info("Deleted session: {}", sessionId);
    }

    // ========== Private Helper Methods ==========

    private UserLessonSessionDetailResponse buildDetailedResponse(UserLessonSession session) {
        UserLessonSessionDetailResponse response = sessionMapper.toDetailResponse(session);

        // Get exercises without exposing correct answers
        List<ExerciseTemplate> exercises = exerciseTemplateRepository
                .findByLessonTemplateIdForUser(
                        session.getLessonTemplate().getId(), 
                        session.getUserId());
        
        List<ExerciseTemplateResponse> exerciseResponses = exerciseTemplateMapper.toResponseListWithoutAnswer(exercises);
        response.setExercises(exerciseResponses);

        return response;
    }
}
