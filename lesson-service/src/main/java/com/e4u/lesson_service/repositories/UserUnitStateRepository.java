package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.UserUnitState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserUnitStateRepository extends JpaRepository<UserUnitState, UUID> {
    List<UserUnitState> findByUserId(UUID userId);
    Optional<UserUnitState> findByUserIdAndUnitId(UUID userId, UUID unitId);
}
