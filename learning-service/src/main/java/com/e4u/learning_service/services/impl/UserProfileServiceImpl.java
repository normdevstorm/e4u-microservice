package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.common.exception.AppException;
import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.common.exception.ResourceNotFoundException;
import com.e4u.learning_service.dtos.request.BaselineResultRequest;
import com.e4u.learning_service.dtos.request.PrivacyConsentRequest;
import com.e4u.learning_service.dtos.request.UserProfilingRequest;
import com.e4u.learning_service.dtos.response.UserProfileResponse;
import com.e4u.learning_service.entities.UserProfile;
import com.e4u.learning_service.mapper.UserProfileMapper;
import com.e4u.learning_service.repositories.UserProfileRepository;
import com.e4u.learning_service.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link UserProfileService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional
    public UserProfileResponse initProfile(UUID userId) {
        log.debug("Initialising profile for userId: {}", userId);

        // Idempotent — return existing profile if already present
        return repository.findByUserIdAndDeletedFalse(userId)
                .map(mapper::toResponse)
                .orElseGet(() -> {
                    UserProfile profile = UserProfile.builder()
                            .userId(userId)
                            .build();
                    profile = repository.save(profile);
                    log.info("Created new profile for userId: {}", userId);
                    return mapper.toResponse(profile);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {
        log.debug("Fetching profile for userId: {}", userId);
        return mapper.toResponse(findByUserIdOrThrow(userId));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfiling(UUID userId, UserProfilingRequest request) {
        log.debug("Updating profiling for userId: {}", userId);
        UserProfile profile = findByUserIdOrThrow(userId);
        mapper.applyProfiling(profile, request);
        profile = repository.save(profile);
        log.info("Profiling updated for userId: {}", userId);
        return mapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updatePrivacyConsent(UUID userId, PrivacyConsentRequest request) {
        log.debug("Updating privacy consent for userId: {}", userId);
        UserProfile profile = findByUserIdOrThrow(userId);
        profile.setPrivacyConsent(request.getPrivacyConsent());
        profile = repository.save(profile);
        log.info("Privacy consent set to {} for userId: {}", request.getPrivacyConsent(), userId);
        return mapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateBaselineResult(UUID userId, BaselineResultRequest request) {
        log.debug("Persisting baseline result for userId: {}", userId);
        UserProfile profile = findByUserIdOrThrow(userId);
        profile.setProficiencyBaseline(request.getCefrLevel());
        profile.setCurrentProficiency(request.getCefrLevel());
        profile = repository.save(profile);
        log.info("Baseline CEFR level '{}' stored for userId: {}", request.getCefrLevel(), userId);
        return mapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse completeOnboarding(UUID userId) {
        log.debug("Completing onboarding for userId: {}", userId);
        UserProfile profile = findByUserIdOrThrow(userId);

        if (Boolean.TRUE.equals(profile.getIsOnboardingComplete())) {
            log.warn("Onboarding already complete for userId: {}", userId);
            return mapper.toResponse(profile);
        }

        profile.setIsOnboardingComplete(true);
        profile = repository.save(profile);
        log.info("Onboarding completed for userId: {}", userId);
        return mapper.toResponse(profile);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private UserProfile findByUserIdOrThrow(UUID userId) {
        return repository.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_PROFILE_NOT_FOUND,
                        "User profile not found for userId: " + userId));
    }
}
