package com.e4u.learning_service.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for UnitBaseWord.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitBaseWordResponse {

    private UUID unitId;
    private UUID wordId;
    private String lemma;
    private String partOfSpeech;
    private String definition;
    private Integer sequenceOrder;
    private Instant createdAt;
}
