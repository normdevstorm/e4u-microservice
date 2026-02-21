package com.e4u.curriculum_service.services;

import com.e4u.curriculum_service.models.request.CurriculumCreateRequest;
import com.e4u.curriculum_service.models.request.CurriculumFilterRequest;
import com.e4u.curriculum_service.models.request.CurriculumUpdateRequest;
import com.e4u.curriculum_service.models.response.CurriculumDetailResponse;
import com.e4u.curriculum_service.models.response.CurriculumResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Curriculum operations.
 */
public interface CurriculumService {

    Page<CurriculumResponse> getAll(int page, int size, String sortBy, String sortDirection);

    CurriculumResponse getById(UUID curriculumId);

    CurriculumDetailResponse getByIdWithDetails(UUID curriculumId);

    Page<CurriculumResponse> filter(CurriculumFilterRequest filterRequest);

    List<CurriculumResponse> getByGoalId(UUID goalId);

    CurriculumResponse create(CurriculumCreateRequest request);

    List<CurriculumResponse> createBatch(List<CurriculumCreateRequest> requests);

    CurriculumResponse partialUpdate(UUID curriculumId, CurriculumUpdateRequest request);

    void softDelete(UUID curriculumId);
}
