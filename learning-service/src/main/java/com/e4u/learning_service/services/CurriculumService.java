package com.e4u.learning_service.services;

import org.springframework.data.domain.Page;

import com.e4u.learning_service.dtos.request.CurriculumCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumFilterRequest;
import com.e4u.learning_service.dtos.request.CurriculumUpdateRequest;
import com.e4u.learning_service.dtos.response.CurriculumDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumResponse;

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

    /** Retrieve all curricula that match the goals of the authenticated user */
    List<CurriculumResponse> getByUser(UUID userId);

    CurriculumResponse create(CurriculumCreateRequest request);

    List<CurriculumResponse> createBatch(List<CurriculumCreateRequest> requests);

    CurriculumResponse partialUpdate(UUID curriculumId, CurriculumUpdateRequest request);

    void softDelete(UUID curriculumId);
}
