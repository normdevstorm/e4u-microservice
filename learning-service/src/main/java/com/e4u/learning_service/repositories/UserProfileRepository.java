package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserIdAndDeletedFalse(UUID userId);

    boolean existsByUserIdAndDeletedFalse(UUID userId);
}
