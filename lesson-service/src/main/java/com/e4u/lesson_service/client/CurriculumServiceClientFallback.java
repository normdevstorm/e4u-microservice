package com.e4u.lesson_service.client;

import com.e4u.lesson_service.client.dto.CurriculumServiceResponse;
import com.e4u.lesson_service.client.dto.CurriculumUnitDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fallback implementation for CurriculumServiceClient.
 * 
 * <p>
 * Provides default responses when the curriculum-service is unavailable.
 * </p>
 */
@Slf4j
@Component
public class CurriculumServiceClientFallback implements CurriculumServiceClient {

    @Override
    public CurriculumServiceResponse<List<CurriculumUnitDTO>> getUnitsByCurriculumId(UUID curriculumId) {
        log.warn("Fallback: Unable to fetch units for curriculum: {}", curriculumId);
        return CurriculumServiceResponse.<List<CurriculumUnitDTO>>builder()
                .success(false)
                .data(Collections.emptyList())
                .message("Curriculum service is currently unavailable")
                .build();
    }

    @Override
    public CurriculumServiceResponse<CurriculumUnitDTO> getUnitById(UUID unitId) {
        log.warn("Fallback: Unable to fetch unit: {}", unitId);
        return CurriculumServiceResponse.<CurriculumUnitDTO>builder()
                .success(false)
                .data(null)
                .message("Curriculum service is currently unavailable")
                .build();
    }
}
