package com.e4u.curriculum_service.services;

import com.e4u.curriculum_service.models.request.CurriculumUnitCreateRequest;
import com.e4u.curriculum_service.models.request.CurriculumUnitFilterRequest;
import com.e4u.curriculum_service.models.request.CurriculumUnitUpdateRequest;
import com.e4u.curriculum_service.models.request.UnitBaseWordsUpdateRequest;
import com.e4u.curriculum_service.models.response.CurriculumUnitDetailResponse;
import com.e4u.curriculum_service.models.response.CurriculumUnitResponse;
import com.e4u.curriculum_service.models.response.UnitBaseWordResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for CurriculumUnit operations.
 */
public interface CurriculumUnitService {

    Page<CurriculumUnitResponse> getAll(int page, int size, String sortBy, String sortDirection);

    CurriculumUnitResponse getById(UUID unitId);

    CurriculumUnitDetailResponse getByIdWithDetails(UUID unitId);

    List<CurriculumUnitResponse> getByCurriculumId(UUID curriculumId);

    Page<CurriculumUnitResponse> filter(CurriculumUnitFilterRequest filterRequest);

    CurriculumUnitResponse create(CurriculumUnitCreateRequest request);

    List<CurriculumUnitResponse> createBatch(List<CurriculumUnitCreateRequest> requests);

    CurriculumUnitResponse partialUpdate(UUID unitId, CurriculumUnitUpdateRequest request);

    List<UnitBaseWordResponse> updateUnitBaseWords(UnitBaseWordsUpdateRequest request);

    void softDelete(UUID unitId);
}
