package com.e4u.curriculum_service.services;

import com.e4u.curriculum_service.models.request.TranslationDictCreateRequest;
import com.e4u.curriculum_service.models.request.TranslationDictFilterRequest;
import com.e4u.curriculum_service.models.request.TranslationDictUpdateRequest;
import com.e4u.curriculum_service.models.response.TranslationDictResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for TranslationDict operations.
 */
public interface TranslationDictService {

    Page<TranslationDictResponse> getAll(int page, int size, String sortBy, String sortDirection);

    TranslationDictResponse getById(UUID id);

    List<TranslationDictResponse> getByWordId(UUID wordId);

    TranslationDictResponse getByWordIdAndLanguage(UUID wordId, String destLang);

    Page<TranslationDictResponse> filter(TranslationDictFilterRequest filterRequest);

    TranslationDictResponse create(TranslationDictCreateRequest request);

    List<TranslationDictResponse> createBatch(List<TranslationDictCreateRequest> requests);

    TranslationDictResponse partialUpdate(UUID id, TranslationDictUpdateRequest request);

    void softDelete(UUID id);
}
