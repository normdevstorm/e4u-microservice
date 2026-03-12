package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.response.OnboardingOptionResponse;

import java.util.List;

/**
 * Provides the static reference lists shown on the onboarding profiling screen.
 */
public interface OnboardingReferenceService {

    /** Returns all occupation options ordered by sort_order. */
    List<OnboardingOptionResponse> getOccupations();

    /** Returns all interest-tag options ordered by sort_order. */
    List<OnboardingOptionResponse> getInterests();

    /**
     * Returns fixed daily time-commitment presets.
     * Values are minutes per day. No DB table needed — the set is stable.
     */
    List<OnboardingOptionResponse> getCommitmentTimeOptions();
}
