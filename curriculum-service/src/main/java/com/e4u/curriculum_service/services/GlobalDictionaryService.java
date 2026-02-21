package com.e4u.curriculum_service.services;

import com.e4u.curriculum_service.models.request.GlobalDictionaryCreateRequest;
import com.e4u.curriculum_service.models.request.GlobalDictionaryFilterRequest;
import com.e4u.curriculum_service.models.request.GlobalDictionaryUpdateRequest;
import com.e4u.curriculum_service.models.response.GlobalDictionaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for GlobalDictionary operations.
 */
public interface GlobalDictionaryService {

    Page<GlobalDictionaryResponse> getAll(int page, int size, String sortBy, String sortDirection);

    GlobalDictionaryResponse getById(UUID wordId);

    GlobalDictionaryResponse getByIdWithTranslations(UUID wordId);

    Page<GlobalDictionaryResponse> filter(GlobalDictionaryFilterRequest filterRequest);

    Page<GlobalDictionaryResponse> search(String keyword, int page, int size);

    GlobalDictionaryResponse create(GlobalDictionaryCreateRequest request);

    List<GlobalDictionaryResponse> createBatch(List<GlobalDictionaryCreateRequest> requests);

    GlobalDictionaryResponse partialUpdate(UUID wordId, GlobalDictionaryUpdateRequest request);

    void softDelete(UUID wordId);
}
