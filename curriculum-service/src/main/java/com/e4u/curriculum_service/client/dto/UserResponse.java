package com.e4u.curriculum_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for User response from user-service.
 * 
 * TODO: Update this DTO when user-service is implemented to match
 * the actual response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String nativeLanguage;
    private String targetLanguage;
    private String proficiencyLevel;
    private Boolean isActive;
    private Instant createdAt;
    private Instant lastLoginAt;
}
