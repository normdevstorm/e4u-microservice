package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a TranslationDict entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDictCreateRequest {

    @NotNull(message = "Word ID is required")
    private UUID wordId;

    @NotBlank(message = "Destination language is required")
    @Size(max = 10, message = "Destination language must not exceed 10 characters")
    private String destLang;

    @NotBlank(message = "Translation is required")
    private String translation;

    private String exampleTranslation;
}
