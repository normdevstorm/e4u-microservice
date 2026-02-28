package com.e4u.learning_service.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for filtering TranslationDict entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationDictFilterRequest {

    private UUID wordId;
    private String destLang;
    private String translationKeyword;

    // Pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "id";
    @Builder.Default
    private String sortDirection = "ASC";
}
