package com.e4u.lesson_service.services.impl;

import com.e4u.lesson_service.common.exception.ErrorCode;
import com.e4u.lesson_service.common.exception.ResourceNotFoundException;
import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.mapper.UserVocabInstanceMapper;
import com.e4u.lesson_service.models.request.UserVocabInstanceCreateRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceFilterRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceUpdateRequest;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import com.e4u.lesson_service.repositories.UserVocabInstanceRepository;
import com.e4u.lesson_service.repositories.specification.UserVocabInstanceSpecification;
import com.e4u.lesson_service.services.UserVocabInstanceService;
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

/**
 * Implementation of UserVocabInstanceService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserVocabInstanceServiceImpl implements UserVocabInstanceService {

    private final UserVocabInstanceRepository repository;
    private final UserVocabInstanceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserVocabInstanceResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all vocabulary instances - page: {}, size: {}", page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Use specification to exclude soft-deleted records
        UserVocabInstanceFilterRequest filter = new UserVocabInstanceFilterRequest();
        return repository.findAll(UserVocabInstanceSpecification.withFilter(filter), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserVocabInstanceResponse getById(UUID id) {
        log.debug("Fetching vocabulary instance by id: {}", id);
        
        UserVocabInstance entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND, 
                        "Vocabulary instance not found with id: " + id));
        
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabInstanceResponse> getByUserId(UUID userId) {
        log.debug("Fetching vocabulary instances for user: {}", userId);
        
        List<UserVocabInstance> entities = repository.findByUserIdAndDeletedFalse(userId);
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserVocabInstanceResponse> filter(UserVocabInstanceFilterRequest filterRequest) {
        log.debug("Filtering vocabulary instances with criteria: {}", filterRequest);
        
        Sort sort = Sort.by(
                Sort.Direction.fromString(filterRequest.getSortDirection()), 
                filterRequest.getSortBy()
        );
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        return repository.findAll(UserVocabInstanceSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public UserVocabInstanceResponse create(UserVocabInstanceCreateRequest request) {
        log.info("Creating new vocabulary instance for user: {}, word: {}", 
                request.getUserId(), request.getWordText());
        
        UserVocabInstance entity = mapper.toEntity(request);
        entity = repository.save(entity);
        
        log.info("Created vocabulary instance with id: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<UserVocabInstanceResponse> createBatch(List<UserVocabInstanceCreateRequest> requests) {
        log.info("Creating batch of {} vocabulary instances", requests.size());
        
        List<UserVocabInstance> entities = mapper.toEntityList(requests);
        entities = repository.saveAll(entities);
        
        log.info("Created {} vocabulary instances", entities.size());
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional
    public UserVocabInstanceResponse partialUpdate(UUID id, UserVocabInstanceUpdateRequest request) {
        log.info("Partially updating vocabulary instance: {}", id);
        
        UserVocabInstance entity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND,
                        "Vocabulary instance not found with id: " + id));
        
        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        
        log.info("Updated vocabulary instance: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        log.info("Soft deleting vocabulary instance: {}", id);
        
        // Verify existence first
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND,
                    "Vocabulary instance not found with id: " + id);
        }
        
        int updated = repository.softDeleteById(id, Instant.now());
        if (updated == 0) {
            log.warn("No vocabulary instance was deleted with id: {}", id);
        } else {
            log.info("Soft deleted vocabulary instance: {}", id);
        }
    }

    @Override
    @Transactional
    public void softDeleteBatch(List<UUID> ids) {
        log.info("Soft deleting {} vocabulary instances", ids.size());
        
        int updated = repository.softDeleteByIds(ids, Instant.now());
        log.info("Soft deleted {} vocabulary instances", updated);
    }
}
