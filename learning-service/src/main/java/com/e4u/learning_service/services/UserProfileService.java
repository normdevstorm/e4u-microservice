package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.BaselineResultRequest;
import com.e4u.learning_service.dtos.request.PrivacyConsentRequest;
import com.e4u.learning_service.dtos.request.UserProfilingRequest;
import com.e4u.learning_service.dtos.response.UserProfileResponse;

import java.util.UUID;

/**
 * Service interface for user profile operations.
 */
public interface UserProfileService {

    /**
     * Idempotently initialise a profile for the given user.
     * Returns the existing profile if one already exists.
     */
    UserProfileResponse initProfile(UUID userId);

    /** Fetch the profile for the current authenticated user. */
    UserProfileResponse getMyProfile(UUID userId);

    /** F-01: Save occupation, interests, and daily time commitment. */
    UserProfileResponse updateProfiling(UUID userId, UserProfilingRequest request);

    /** F-03: Record privacy consent. */
    UserProfileResponse updatePrivacyConsent(UUID userId, PrivacyConsentRequest request);

    /** F-02 persist: Store the CEFR baseline level. */
    UserProfileResponse updateBaselineResult(UUID userId, BaselineResultRequest request);

    /** Mark onboarding as fully complete. */
    UserProfileResponse completeOnboarding(UUID userId);
}
