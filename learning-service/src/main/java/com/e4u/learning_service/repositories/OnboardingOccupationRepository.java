package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.OnboardingOccupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OnboardingOccupationRepository extends JpaRepository<OnboardingOccupation, UUID> {

    /** Returns all occupations ordered by sort_order ASC for display. */
    List<OnboardingOccupation> findAllByOrderBySortOrderAsc();
}
