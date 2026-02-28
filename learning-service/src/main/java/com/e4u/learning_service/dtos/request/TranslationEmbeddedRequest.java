package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded request DTO for creating a translation within a GlobalDictionary
 * request.
 * Does not require wordId as it's embedded in the parent request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEmbeddedRequest {

    @NotBlank(message = "Destination language is required")
    @Size(max = 10, message = "Destination language must not exceed 10 characters")
    private String destLang;

    @NotBlank(message = "Translation is required")
    private String translation;

    private String exampleTranslation;
}
