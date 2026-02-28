package com.e4u.learning_service.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for TranslationDict.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranslationDictResponse {

    private UUID id;
    private UUID wordId;
    private String lemma;
    private String destLang;
    private String translation;
    private String exampleTranslation;
    private Instant createdAt;
    private Instant updatedAt;
}
