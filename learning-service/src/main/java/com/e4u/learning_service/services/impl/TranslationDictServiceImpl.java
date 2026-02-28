package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.common.exception.AppException;
import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.common.exception.ResourceNotFoundException;
import com.e4u.learning_service.dtos.request.TranslationDictCreateRequest;
import com.e4u.learning_service.dtos.request.TranslationDictFilterRequest;
import com.e4u.learning_service.dtos.request.TranslationDictUpdateRequest;
import com.e4u.learning_service.dtos.response.TranslationDictResponse;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.TranslationDict;
import com.e4u.learning_service.mapper.TranslationDictMapper;
import com.e4u.learning_service.repositories.GlobalDictionaryRepository;
import com.e4u.learning_service.repositories.TranslationDictRepository;
import com.e4u.learning_service.repositories.specification.TranslationDictSpecification;
import com.e4u.learning_service.services.TranslationDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for TranslationDict operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationDictServiceImpl implements TranslationDictService {

    private final TranslationDictRepository repository;
    private final GlobalDictionaryRepository globalDictionaryRepository;
    private final TranslationDictMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TranslationDictResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all translations - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDeletedFalse(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDictResponse getById(UUID id) {
        log.debug("Fetching translation by id: {}", id);
        TranslationDict entity = findByIdOrThrow(id);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationDictResponse> getByWordId(UUID wordId) {
        log.debug("Fetching translations by word id: {}", wordId);
        return repository.findByWord_IdAndDeletedFalse(wordId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TranslationDictResponse getByWordIdAndLanguage(UUID wordId, String destLang) {
        log.debug("Fetching translation - wordId: {}, destLang: {}", wordId, destLang);
        TranslationDict entity = repository.findByWord_IdAndDestLangAndDeletedFalse(wordId, destLang)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRANSLATION_NOT_FOUND,
                        "Translation not found for word: " + wordId + " and language: " + destLang));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TranslationDictResponse> filter(TranslationDictFilterRequest filterRequest) {
        log.debug("Filtering translations with request: {}", filterRequest);
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        return repository.findAll(TranslationDictSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public TranslationDictResponse create(TranslationDictCreateRequest request) {
        log.info("Creating new translation for word: {} in language: {}", request.getWordId(), request.getDestLang());

        // Check if translation already exists
        if (repository.existsByWord_IdAndDestLangAndDeletedFalse(request.getWordId(), request.getDestLang())) {
            throw new AppException(ErrorCode.TRANSLATION_ALREADY_EXISTS,
                    "Translation already exists for word: " + request.getWordId() + " in language: "
                            + request.getDestLang());
        }

        // Get word
        GlobalDictionary word = globalDictionaryRepository.findByIdAndDeletedFalse(request.getWordId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GLOBAL_DICTIONARY_NOT_FOUND,
                        "Word not found with id: " + request.getWordId()));

        TranslationDict entity = mapper.toEntity(request);
        entity.setWord(word);
        entity = repository.save(entity);

        log.info("Created translation with id: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<TranslationDictResponse> createBatch(List<TranslationDictCreateRequest> requests) {
        log.info("Creating {} translations in batch", requests.size());
        List<TranslationDictResponse> responses = new ArrayList<>();

        for (TranslationDictCreateRequest request : requests) {
            if (!repository.existsByWord_IdAndDestLangAndDeletedFalse(request.getWordId(), request.getDestLang())) {
                GlobalDictionary word = globalDictionaryRepository.findByIdAndDeletedFalse(request.getWordId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GLOBAL_DICTIONARY_NOT_FOUND,
                                "Word not found with id: " + request.getWordId()));

                TranslationDict entity = mapper.toEntity(request);
                entity.setWord(word);
                entity = repository.save(entity);
                responses.add(mapper.toResponse(entity));
            }
        }

        log.info("Created {} translations", responses.size());
        return responses;
    }

    @Override
    @Transactional
    public TranslationDictResponse partialUpdate(UUID id, TranslationDictUpdateRequest request) {
        log.info("Updating translation: {}", id);
        TranslationDict entity = findByIdOrThrow(id);
        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        log.info("Updated translation: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        log.info("Soft deleting translation: {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.TRANSLATION_NOT_FOUND,
                    "Translation not found with id: " + id);
        }
        repository.softDeleteById(id, Instant.now());
        log.info("Soft deleted translation: {}", id);
    }

    private TranslationDict findByIdOrThrow(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TRANSLATION_NOT_FOUND,
                        "Translation not found with id: " + id));
    }
}
