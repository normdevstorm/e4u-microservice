package com.e4u.learning_service.services;

import java.util.UUID;

/**
 * Synchronizes aggregated unit state from lesson sessions.
 */
public interface UserUnitStateSyncService {

    /**
     * Recalculate and persist unit status from all sessions in the unit.
     */
    void syncFromSessions(UUID userUnitStateId);
}
