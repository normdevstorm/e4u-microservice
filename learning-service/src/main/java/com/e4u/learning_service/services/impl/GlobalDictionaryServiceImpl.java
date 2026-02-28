package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.common.exception.ResourceNotFoundException;
import com.e4u.learning_service.dtos.request.GlobalDictionaryCreateRequest;
import com.e4u.learning_service.dtos.request.GlobalDictionaryFilterRequest;
import com.e4u.learning_service.dtos.request.GlobalDictionaryUpdateRequest;
import com.e4u.learning_service.dtos.response.GlobalDictionaryResponse;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.TranslationDict;
import com.e4u.learning_service.mapper.GlobalDictionaryMapper;
import com.e4u.learning_service.repositories.GlobalDictionaryRepository;
import com.e4u.learning_service.repositories.TranslationDictRepository;
import com.e4u.learning_service.repositories.specification.GlobalDictionarySpecification;
import com.e4u.learning_service.services.GlobalDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for GlobalDictionary operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalDictionaryServiceImpl implements GlobalDictionaryService {

    private final GlobalDictionaryRepository repository;
    private final TranslationDictRepository translationDictRepository;
    private final GlobalDictionaryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<GlobalDictionaryResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all dictionary entries - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDeletedFalse(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalDictionaryResponse getById(UUID wordId) {
        log.debug("Fetching dictionary entry by id: {}", wordId);
        GlobalDictionary entity = findByIdOrThrow(wordId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalDictionaryResponse getByIdWithTranslations(UUID wordId) {
        log.debug("Fetching dictionary entry with translations by id: {}", wordId);
        GlobalDictionary entity = repository.findByIdWithTranslations(wordId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GLOBAL_DICTIONARY_NOT_FOUND,
                        "Word not found with id: " + wordId));
        return mapper.toResponseWithTranslations(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GlobalDictionaryResponse> filter(GlobalDictionaryFilterRequest filterRequest) {
        log.debug("Filtering dictionary entries with request: {}", filterRequest);
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        return repository.findAll(GlobalDictionarySpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GlobalDictionaryResponse> search(String keyword, int page, int size) {
        log.debug("Searching dictionary entries with keyword: {}", keyword);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lemma").ascending());
        return repository.searchByLemma(keyword, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public GlobalDictionaryResponse create(GlobalDictionaryCreateRequest request) {
        log.info("Creating new dictionary entry: {}", request.getLemma());
        GlobalDictionary entity = mapper.toEntity(request);

        // Handle embedded translations if provided
        if (request.getTranslations() != null && !request.getTranslations().isEmpty()) {
            log.debug("Creating {} translations for word: {}", request.getTranslations().size(), request.getLemma());
            request.getTranslations().forEach(translationRequest -> {
                TranslationDict translation = TranslationDict.builder()
                        .destLang(translationRequest.getDestLang())
                        .translation(translationRequest.getTranslation())
                        .exampleTranslation(translationRequest.getExampleTranslation())
                        .build();
                entity.addTranslation(translation);
            });
        }

        GlobalDictionary savedEntity = repository.save(entity);
        log.info("Created dictionary entry with id: {} and {} translations",
                savedEntity.getId(), savedEntity.getTranslations().size());

        // Return with translations if they were provided
        if (request.getTranslations() != null && !request.getTranslations().isEmpty()) {
            return mapper.toResponseWithTranslations(savedEntity);
        }
        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    public List<GlobalDictionaryResponse> createBatch(List<GlobalDictionaryCreateRequest> requests) {
        log.info("Creating {} dictionary entries in batch", requests.size());
        List<GlobalDictionary> entities = requests.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        entities = repository.saveAll(entities);
        log.info("Created {} dictionary entries", entities.size());
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional
    public GlobalDictionaryResponse partialUpdate(UUID wordId, GlobalDictionaryUpdateRequest request) {
        log.info("Updating dictionary entry: {}", wordId);
        GlobalDictionary entity = findByIdOrThrow(wordId);
        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        log.info("Updated dictionary entry: {}", wordId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID wordId) {
        log.info("Soft deleting dictionary entry: {}", wordId);
        if (!repository.existsById(wordId)) {
            throw new ResourceNotFoundException(ErrorCode.GLOBAL_DICTIONARY_NOT_FOUND,
                    "Word not found with id: " + wordId);
        }
        // Also soft delete associated translations
        translationDictRepository.softDeleteByWordId(wordId, Instant.now());
        repository.softDeleteById(wordId, Instant.now());
        log.info("Soft deleted dictionary entry: {}", wordId);
    }

    private GlobalDictionary findByIdOrThrow(UUID wordId) {
        return repository.findByIdAndDeletedFalse(wordId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GLOBAL_DICTIONARY_NOT_FOUND,
                        "Word not found with id: " + wordId));
    }
}
