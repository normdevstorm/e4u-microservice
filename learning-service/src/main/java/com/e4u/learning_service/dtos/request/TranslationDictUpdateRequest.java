package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for partially updating a TranslationDict entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDictUpdateRequest {

    @Size(max = 10, message = "Destination language must not exceed 10 characters")
    private String destLang;

    private String translation;

    private String exampleTranslation;
}
