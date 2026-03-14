package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.entities.UserLessonSession;
import com.e4u.learning_service.entities.UserUnitState;
import com.e4u.learning_service.repositories.LessonTemplateRepository;
import com.e4u.learning_service.repositories.UserLessonSessionRepository;
import com.e4u.learning_service.repositories.UserUnitStateRepository;
import com.e4u.learning_service.services.UserUnitStateSyncService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUnitStateSyncServiceImpl implements UserUnitStateSyncService {

    private final UserUnitStateRepository userUnitStateRepository;
    private final UserLessonSessionRepository sessionRepository;
    private final LessonTemplateRepository lessonTemplateRepository;

    @Override
    @Transactional
    public void syncFromSessions(UUID userUnitStateId) {
        UserUnitState unitState = userUnitStateRepository.findById(userUnitStateId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserUnitState not found with ID: " + userUnitStateId));

        List<UserLessonSession> sessions = sessionRepository.findByUserUnitStateId(userUnitStateId);
        long totalLessonsInUnit = lessonTemplateRepository.countByUnitId(unitState.getUnitId());

        long completedLessonsAtLeastOnce = sessions.stream()
                .filter(s -> s.getStatus() == UserLessonSession.SessionStatus.COMPLETED)
                .map(s -> s.getLessonTemplate() != null ? s.getLessonTemplate().getId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet())
                .size();

        final UserUnitState.UnitStatus nextStatus;
        if (totalLessonsInUnit > 0 && completedLessonsAtLeastOnce >= totalLessonsInUnit) {
            // Business rule: once every lesson in the unit has been completed at least
            // once,
            // unit remains COMPLETED even if user re-learns and creates in-progress
            // sessions.
            nextStatus = UserUnitState.UnitStatus.COMPLETED;
        } else if (sessions.isEmpty()) {
            nextStatus = UserUnitState.UnitStatus.NOT_STARTED;
        } else {
            boolean anyStarted = sessions.stream()
                    .anyMatch(s -> s.getStatus() != UserLessonSession.SessionStatus.NOT_STARTED);

            if (anyStarted) {
                nextStatus = UserUnitState.UnitStatus.IN_PROGRESS;
            } else {
                nextStatus = UserUnitState.UnitStatus.NOT_STARTED;
            }
        }

        unitState.setStatus(nextStatus);
        unitState.setLastInteractionAt(LocalDateTime.now());

        // TODO: derive proficiency_score from weighted session performance (accuracy,
        // retries, recency).

        userUnitStateRepository.save(unitState);

        log.debug("Synced unit state {} => {} using {} sessions, completedLessonsAtLeastOnce={}/{}",
                userUnitStateId, nextStatus, sessions.size(), completedLessonsAtLeastOnce, totalLessonsInUnit);
    }
}
