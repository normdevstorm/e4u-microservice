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

        // Fetch ALL sessions for this user+lesson, newest first
        List<UserLessonSession> allSessions = sessionRepository
                .findAllByUserIdAndLessonTemplateId(request.getUserId(), request.getLessonTemplateId());

        UserLessonSession session;

        if (allSessions.isEmpty()) {
            // ---- 2a: No prior history — create a fresh IN_PROGRESS session ----
            log.info("No prior session found for user: {} lesson: {} — creating new.",
                    request.getUserId(), request.getLessonTemplateId());
            session = buildAndSaveNewSession(request, null);

        } else {
            UserLessonSession latestSession = allSessions.get(0); // newest
            boolean hasCompletedSession = allSessions.stream()
                    .anyMatch(s -> s.getStatus() == SessionStatus.COMPLETED);

            if (!hasCompletedSession) {
                // ---- No completed session yet — resume the latest if it still has remaining
                // exercises ----
                boolean canResume = latestSession.getCompletedItems() != null
                        && latestSession.getTotalItems() != null
                        && latestSession.getCompletedItems() < latestSession.getTotalItems();

                if (canResume) {
                    log.info("Resuming in-progress session: {} ({}/{} done).",
                            latestSession.getId(),
                            latestSession.getCompletedItems(), latestSession.getTotalItems());
                    session = latestSession;
                } else {
                    // Edge case: session has no exercises or all done but not marked complete
                    log.info("Latest session {} has no remaining exercises — creating new.",
                            latestSession.getId());
                    session = buildAndSaveNewSession(request, latestSession.getTotalItems());
                }

            } else {
                // ---- 2b: At least one completed session exists ----
                if (latestSession.getStatus() == SessionStatus.COMPLETED) {
                    // ---- 2b2: The latest session is itself COMPLETED — start fresh ----
                    log.info("Latest session {} is COMPLETED — creating fresh IN_PROGRESS session.",
                            latestSession.getId());
                    session = buildAndSaveNewSession(request, latestSession.getTotalItems());

                } else {
                    // ---- 2b1: An in-progress/paused session was created AFTER a completion —
                    // resume it ----
                    boolean canResume = latestSession.getCompletedItems() != null
                            && latestSession.getTotalItems() != null
                            && latestSession.getCompletedItems() < latestSession.getTotalItems();

                    if (canResume) {
                        log.info("Resuming post-completion session: {} ({}/{} done).",
                                latestSession.getId(),
                                latestSession.getCompletedItems(), latestSession.getTotalItems());
                        session = latestSession;
                    } else {
                        log.info("Post-completion session {} exhausted — creating new session.",
                                latestSession.getId());
                        session = buildAndSaveNewSession(request, latestSession.getTotalItems());
                    }
                }
            }
        }

        // Transition to IN_PROGRESS if not already running
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            List<ExerciseTemplate> exercises = exerciseTemplateRepository
                    .findByLessonTemplateIdForUser(request.getLessonTemplateId(), request.getUserId());
            session.start(exercises.size());
            session = sessionRepository.save(session);
        }

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

        // Return the latest in-progress/paused session if one exists
        List<UserLessonSession> allSessions = sessionRepository
                .findAllByUserIdAndLessonTemplateId(userId, lessonTemplateId);

        if (!allSessions.isEmpty()) {
            UserLessonSession latest = allSessions.get(0);
            if (latest.getStatus() != SessionStatus.COMPLETED) {
                return sessionMapper.toResponse(latest);
            }
        }

        // No usable session — delegate to startOrResumeSession for full creation logic
        UserLessonSessionStartRequest request = UserLessonSessionStartRequest.builder()
                .userId(userId)
                .lessonTemplateId(lessonTemplateId)
                .userUnitStateId(userUnitStateId)
                .build();

        UserLessonSessionDetailResponse detail = startOrResumeSession(request);

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

    /**
     * Creates, persists, and returns a brand-new NOT_STARTED session.
     * If {@code inheritedTotalItems} is provided it is used as the initial
     * {@code totalItems}
     * so callers know the exercise count without an extra query. The real count is
     * refreshed when {@code session.start()} is called before the first IN_PROGRESS
     * save.
     */
    private UserLessonSession buildAndSaveNewSession(
            UserLessonSessionStartRequest request,
            Integer inheritedTotalItems) {

        LessonTemplate lessonTemplate = lessonTemplateRepository.findById(request.getLessonTemplateId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "LessonTemplate not found with ID: " + request.getLessonTemplateId()));

        UserUnitState userUnitState = null;
        if (request.getUserUnitStateId() != null) {
            userUnitState = userUnitStateRepository.findById(request.getUserUnitStateId()).orElse(null);
        }

        UserLessonSession session = UserLessonSession.builder()
                .userId(request.getUserId())
                .lessonTemplate(lessonTemplate)
                .userUnitState(userUnitState)
                .status(SessionStatus.NOT_STARTED)
                .totalItems(inheritedTotalItems != null ? inheritedTotalItems : 0)
                .completedItems(0)
                .correctItems(0)
                .build();

        session = sessionRepository.save(session);
        log.info("Created new session with ID: {}", session.getId());
        return session;
    }

    private UserLessonSessionDetailResponse buildDetailedResponse(UserLessonSession session) {
        UserLessonSessionDetailResponse response = sessionMapper.toDetailResponse(session);

        // Get exercises without exposing correct answers
        List<ExerciseTemplate> exercises = exerciseTemplateRepository
                .findByLessonTemplateIdForUser(
                        session.getLessonTemplate().getId(),
                        session.getUserId());

        List<ExerciseTemplateResponse> exerciseResponses = exerciseTemplateMapper
                .toResponseListWithoutAnswer(exercises);
        response.setExercises(exerciseResponses);

        return response;
    }
}
