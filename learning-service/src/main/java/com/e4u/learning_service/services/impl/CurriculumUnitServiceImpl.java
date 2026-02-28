package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.common.exception.ResourceNotFoundException;
import com.e4u.learning_service.dtos.request.CurriculumUnitCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumUnitFilterRequest;
import com.e4u.learning_service.dtos.request.CurriculumUnitUpdateRequest;
import com.e4u.learning_service.dtos.request.UnitBaseWordsUpdateRequest;
import com.e4u.learning_service.dtos.response.CurriculumUnitDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumUnitResponse;
import com.e4u.learning_service.dtos.response.UnitBaseWordResponse;
import com.e4u.learning_service.entities.Curriculum;
import com.e4u.learning_service.entities.CurriculumUnit;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.UnitBaseWord;
import com.e4u.learning_service.mapper.CurriculumUnitMapper;
import com.e4u.learning_service.mapper.GlobalDictionaryMapper;
import com.e4u.learning_service.mapper.UnitBaseWordMapper;
import com.e4u.learning_service.repositories.CurriculumRepository;
import com.e4u.learning_service.repositories.CurriculumUnitRepository;
import com.e4u.learning_service.repositories.GlobalDictionaryRepository;
import com.e4u.learning_service.repositories.UnitBaseWordRepository;
import com.e4u.learning_service.repositories.specification.CurriculumUnitSpecification;
import com.e4u.learning_service.services.CurriculumUnitService;
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
 * Service implementation for CurriculumUnit operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurriculumUnitServiceImpl implements CurriculumUnitService {

    private final CurriculumUnitRepository repository;
    private final CurriculumRepository curriculumRepository;
    private final GlobalDictionaryRepository globalDictionaryRepository;
    private final UnitBaseWordRepository unitBaseWordRepository;
    private final CurriculumUnitMapper mapper;
    private final GlobalDictionaryMapper globalDictionaryMapper;
    private final UnitBaseWordMapper unitBaseWordMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CurriculumUnitResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all curriculum units - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDeletedFalse(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumUnitResponse getById(UUID unitId) {
        log.debug("Fetching curriculum unit by id: {}", unitId);
        CurriculumUnit entity = findByIdOrThrow(unitId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumUnitDetailResponse getByIdWithDetails(UUID unitId) {
        log.debug("Fetching curriculum unit with details by id: {}", unitId);
        CurriculumUnit entity = repository.findByIdWithBaseWords(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CURRICULUM_UNIT_NOT_FOUND,
                        "Curriculum unit not found with id: " + unitId));
        return mapper.toDetailResponse(entity, globalDictionaryMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumUnitResponse> getByCurriculumId(UUID curriculumId) {
        log.debug("Fetching units by curriculum id: {}", curriculumId);
        return repository.findByCurriculum_IdAndDeletedFalseOrderByDefaultOrderAsc(curriculumId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurriculumUnitResponse> filter(CurriculumUnitFilterRequest filterRequest) {
        log.debug("Filtering curriculum units with request: {}", filterRequest);
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        return repository.findAll(CurriculumUnitSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public CurriculumUnitResponse create(CurriculumUnitCreateRequest request) {
        log.info("Creating new curriculum unit: {}", request.getUnitName());

        // Get curriculum
        Curriculum curriculum = curriculumRepository.findByIdAndDeletedFalse(request.getCurriculumId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CURRICULUM_NOT_FOUND,
                        "Curriculum not found with id: " + request.getCurriculumId()));

        CurriculumUnit entity = mapper.toEntity(request);
        entity.setCurriculum(curriculum);

        // Set default order if not provided
        if (entity.getDefaultOrder() == null) {
            Integer maxOrder = repository.findMaxOrderByCurriculumId(request.getCurriculumId());
            entity.setDefaultOrder(maxOrder + 1);
        }

        entity = repository.save(entity);
        log.info("Created curriculum unit with id: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<CurriculumUnitResponse> createBatch(List<CurriculumUnitCreateRequest> requests) {
        log.info("Creating {} curriculum units in batch", requests.size());
        List<CurriculumUnit> entities = new ArrayList<>();

        for (CurriculumUnitCreateRequest request : requests) {
            Curriculum curriculum = curriculumRepository.findByIdAndDeletedFalse(request.getCurriculumId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CURRICULUM_NOT_FOUND,
                            "Curriculum not found with id: " + request.getCurriculumId()));

            CurriculumUnit entity = mapper.toEntity(request);
            entity.setCurriculum(curriculum);

            if (entity.getDefaultOrder() == null) {
                Integer maxOrder = repository.findMaxOrderByCurriculumId(request.getCurriculumId());
                entity.setDefaultOrder(maxOrder + 1);
            }

            entities.add(entity);
        }

        entities = repository.saveAll(entities);
        log.info("Created {} curriculum units", entities.size());
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional
    public CurriculumUnitResponse partialUpdate(UUID unitId, CurriculumUnitUpdateRequest request) {
        log.info("Updating curriculum unit: {}", unitId);
        CurriculumUnit entity = findByIdOrThrow(unitId);
        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        log.info("Updated curriculum unit: {}", unitId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<UnitBaseWordResponse> updateUnitBaseWords(UnitBaseWordsUpdateRequest request) {
        log.info("Updating base words for unit: {}", request.getUnitId());

        CurriculumUnit unit = findByIdOrThrow(request.getUnitId());

        // Remove words
        if (request.getRemoveWordIds() != null && !request.getRemoveWordIds().isEmpty()) {
            for (UUID wordId : request.getRemoveWordIds()) {
                unitBaseWordRepository.hardDeleteByUnitIdAndWordId(request.getUnitId(), wordId);
            }
            log.info("Removed {} words from unit {}", request.getRemoveWordIds().size(), request.getUnitId());
        }

        // Add words
        if (request.getAddWordIds() != null && !request.getAddWordIds().isEmpty()) {
            Integer maxOrder = unitBaseWordRepository.findMaxSequenceOrderByUnitId(request.getUnitId());
            int order = maxOrder + 1;

            for (UUID wordId : request.getAddWordIds()) {
                if (!unitBaseWordRepository.existsByUnitIdAndWordIdAndDeletedFalse(request.getUnitId(), wordId)) {
                    GlobalDictionary word = globalDictionaryRepository.findByIdAndDeletedFalse(wordId)
                            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GLOBAL_DICTIONARY_NOT_FOUND,
                                    "Word not found with id: " + wordId));

                    UnitBaseWord baseWord = UnitBaseWord.builder()
                            .unitId(unit.getId())
                            .wordId(word.getId())
                            .unit(unit)
                            .word(word)
                            .sequenceOrder(order++)
                            .build();
                    unitBaseWordRepository.save(baseWord);
                }
            }
            log.info("Added {} words to unit {}", request.getAddWordIds().size(), request.getUnitId());
        }

        // Return updated list
        List<UnitBaseWord> baseWords = unitBaseWordRepository.findByUnitIdWithWordDetails(request.getUnitId());
        return unitBaseWordMapper.toResponseList(baseWords);
    }

    @Override
    @Transactional
    public void softDelete(UUID unitId) {
        log.info("Soft deleting curriculum unit: {}", unitId);
        if (!repository.existsById(unitId)) {
            throw new ResourceNotFoundException(ErrorCode.CURRICULUM_UNIT_NOT_FOUND,
                    "Curriculum unit not found with id: " + unitId);
        }
        // Also soft delete associated base words
        unitBaseWordRepository.softDeleteByUnitId(unitId, Instant.now());
        repository.softDeleteById(unitId, Instant.now());
        log.info("Soft deleted curriculum unit: {}", unitId);
    }

    private CurriculumUnit findByIdOrThrow(UUID unitId) {
        return repository.findByIdAndDeletedFalse(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CURRICULUM_UNIT_NOT_FOUND,
                        "Curriculum unit not found with id: " + unitId));
    }
}
