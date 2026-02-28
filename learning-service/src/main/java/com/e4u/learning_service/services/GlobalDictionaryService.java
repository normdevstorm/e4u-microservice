package com.e4u.learning_service.services;

import org.springframework.data.domain.Page;

import com.e4u.learning_service.dtos.request.GlobalDictionaryCreateRequest;
import com.e4u.learning_service.dtos.request.GlobalDictionaryFilterRequest;
import com.e4u.learning_service.dtos.request.GlobalDictionaryUpdateRequest;
import com.e4u.learning_service.dtos.response.GlobalDictionaryResponse;

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
