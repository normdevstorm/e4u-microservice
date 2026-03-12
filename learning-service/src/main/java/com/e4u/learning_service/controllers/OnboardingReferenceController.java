package com.e4u.learning_service.controllers;

import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.OnboardingOptionResponse;
import com.e4u.learning_service.services.OnboardingReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Serves reference/lookup data consumed during the onboarding profiling step.
 * Base path: /v1/onboarding/options
 *
 * <p>
 * No authentication required — these lists are the same for every user
 * and change only on admin-initiated redeployment.
 */
@RestController
@RequestMapping("/v1/onboarding/options")
@RequiredArgsConstructor
@Tag(name = "Onboarding Reference", description = "Reference lists for the onboarding profiling screen")
public class OnboardingReferenceController {

    private final OnboardingReferenceService referenceService;

    @GetMapping("/occupations")
    @Operation(summary = "List occupation options", description = "Returns all available occupation options ordered by sort_order. "
            +
            "The user's selection is stored as the 'code' value.")
    public ResponseEntity<BaseResponse<List<OnboardingOptionResponse>>> getOccupations() {
        return ResponseEntity.ok(
                BaseResponse.ok(referenceService.getOccupations(), "Occupations fetched"));
    }

    @GetMapping("/interests")
    @Operation(summary = "List interest-tag options", description = "Returns all available interest tags ordered by sort_order. "
            +
            "The user may select multiple; selections are stored as 'code' values.")
    public ResponseEntity<BaseResponse<List<OnboardingOptionResponse>>> getInterests() {
        return ResponseEntity.ok(
                BaseResponse.ok(referenceService.getInterests(), "Interests fetched"));
    }

    @GetMapping("/commitment-times")
    @Operation(summary = "List daily time-commitment presets", description = "Returns fixed preset options for daily study-time commitment (in minutes). "
            +
            "The 'code' value (numeric string) should be stored as dailyTimeCommitment.")
    public ResponseEntity<BaseResponse<List<OnboardingOptionResponse>>> getCommitmentTimes() {
        return ResponseEntity.ok(
                BaseResponse.ok(referenceService.getCommitmentTimeOptions(), "Commitment times fetched"));
    }
}
