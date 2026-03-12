package com.e4u.learning_service.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for user profile data, including onboarding status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private UUID profileId;
    private UUID userId;
    private String occupation;
    private List<String> interests;
    private String proficiencyBaseline;
    private String currentProficiency;
    private Integer dailyTimeCommitment;
    private Boolean privacyConsent;
    private Boolean isOnboardingComplete;
    private Instant createdAt;
    private Instant updatedAt;
}
