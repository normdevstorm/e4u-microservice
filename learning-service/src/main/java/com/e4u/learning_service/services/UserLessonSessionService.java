package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.UserLessonSessionStartRequest;
import com.e4u.learning_service.dtos.response.UserLessonSessionDetailResponse;
import com.e4u.learning_service.dtos.response.UserLessonSessionResponse;
import com.e4u.learning_service.entities.UserLessonSession.SessionStatus;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing user lesson sessions.
 * Handles session lifecycle: start, progress, pause, complete.
 */
public interface UserLessonSessionService {

    /**
     * Start or resume a lesson session for a user
     * If session exists and is not completed, resume it
     * Otherwise, create a new session
     */
    UserLessonSessionDetailResponse startOrResumeSession(UserLessonSessionStartRequest request);

    /**
     * Get session by ID
     */
    UserLessonSessionResponse getSessionById(UUID sessionId);

    /**
     * Get detailed session with exercises and attempts
     */
    UserLessonSessionDetailResponse getSessionDetail(UUID sessionId);

    /**
     * Get all sessions for a user
     */
    List<UserLessonSessionResponse> getSessionsByUser(UUID userId);

    /**
     * Get all sessions for a user within a specific unit
     */
    List<UserLessonSessionResponse> getSessionsByUserAndUnit(UUID userId, UUID userUnitStateId);

    /**
     * Get sessions by status for a user
     */
    List<UserLessonSessionResponse> getSessionsByUserAndStatus(UUID userId, SessionStatus status);

    /**
     * Pause a session
     */
    UserLessonSessionResponse pauseSession(UUID sessionId);

    /**
     * Complete a session
     */
    UserLessonSessionResponse completeSession(UUID sessionId);

    /**
     * Update session progress (called after each exercise attempt)
     */
    UserLessonSessionResponse updateSessionProgress(UUID sessionId, boolean isCorrect);

    /**
     * Get or create session for a user and lesson template
     */
    UserLessonSessionResponse getOrCreateSession(UUID userId, UUID lessonTemplateId, UUID userUnitStateId);

    /**
     * Delete a session (admin only)
     */
    void deleteSession(UUID sessionId);
}
