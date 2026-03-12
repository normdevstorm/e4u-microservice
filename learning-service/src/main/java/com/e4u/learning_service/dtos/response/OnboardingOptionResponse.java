package com.e4u.learning_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic option DTO used for onboarding reference data lists
 * (occupations, interests, commitment-time presets).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingOptionResponse {

    /**
     * UUID or a stable string key (for commitment-time presets, id ==
     * value.toString())
     */
    private String id;

    /** Machine-readable code, e.g. "student", "travel", "15" */
    private String code;

    /** Human-readable label displayed in the UI */
    private String label;

    /** Display ordering hint */
    private Integer sortOrder;
}
