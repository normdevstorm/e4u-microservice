package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.OnboardingInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OnboardingInterestRepository extends JpaRepository<OnboardingInterest, UUID> {

    /** Returns all interest tags ordered by sort_order ASC for display. */
    List<OnboardingInterest> findAllByOrderBySortOrderAsc();
}
