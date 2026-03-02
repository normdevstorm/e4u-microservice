package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for creating or updating user vocab progress.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVocabProgressCreateRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Word ID is required")
    private UUID wordId;

    private UUID activeContextId;

    private Float relevanceScore;
}
