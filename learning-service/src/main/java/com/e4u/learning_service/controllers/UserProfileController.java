package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.BaselineResultRequest;
import com.e4u.learning_service.dtos.request.PrivacyConsentRequest;
import com.e4u.learning_service.dtos.request.UserProfilingRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.UserProfileResponse;
import com.e4u.learning_service.services.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user profile and onboarding operations.
 * Base path: /v1/profile
 */
@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "APIs for user profile and onboarding management")
public class UserProfileController {

    private final UserProfileService service;

    @PostMapping("/init")
    @Operation(summary = "Initialise user profile", description = "Idempotently creates a profile for the authenticated user. "
            +
            "Call this once after every login. Returns the existing profile if already present.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile initialised or already exists"),
    })
    public ResponseEntity<BaseResponse<UserProfileResponse>> initProfile() {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse result = service.initProfile(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ok(result, "Profile initialised"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get own profile", description = "Returns the full profile of the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "404", description = "Profile not found — call /init first"),
    })
    public ResponseEntity<BaseResponse<UserProfileResponse>> getMyProfile() {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse result = service.getMyProfile(userId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @PutMapping("/me")
    @Operation(summary = "Update profiling data (F-01)", description = "Saves occupation, interests, and daily time commitment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profiling saved"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
    })
    public ResponseEntity<BaseResponse<UserProfileResponse>> updateProfiling(
            @Valid @RequestBody UserProfilingRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse result = service.updateProfiling(userId, request);
        return ResponseEntity.ok(BaseResponse.ok(result, "Profiling saved"));
    }

    @PatchMapping("/me/privacy")
    @Operation(summary = "Accept privacy consent (F-03)", description = "Records the user's privacy consent decision")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consent recorded"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
    })
    public ResponseEntity<BaseResponse<UserProfileResponse>> updatePrivacyConsent(
            @Valid @RequestBody PrivacyConsentRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse result = service.updatePrivacyConsent(userId, request);
        return ResponseEntity.ok(BaseResponse.ok(result, "Privacy consent recorded"));
    }

    @PatchMapping("/me/baseline")
    @Operation(summary = "Persist baseline CEFR result (F-02)", description = "Stores the CEFR level returned by POST /v1/baseline/evaluate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Baseline result stored"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
    })
    public ResponseEntity<BaseResponse<UserProfileResponse>> updateBaselineResult(
            @Valid @RequestBody BaselineResultRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse result = service.updateBaselineResult(userId, request);
        return ResponseEntity.ok(BaseResponse.ok(result, "Baseline result stored"));
    }

    @PatchMapping("/me/onboarding/complete")
    @Operation(summary = "Complete onboarding", description = "Marks the onboarding flow as fully complete for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Onboarding marked complete"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
    })
    public ResponseEntity<BaseResponse<UserProfileResponse>> completeOnboarding() {
        UUID userId = SecurityUtil.getCurrentUserId();
        UserProfileResponse result = service.completeOnboarding(userId);
        return ResponseEntity.ok(BaseResponse.ok(result, "Onboarding complete"));
    }
}
