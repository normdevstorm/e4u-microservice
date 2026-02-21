package com.e4u.lesson_service.services;

import com.e4u.lesson_service.models.request.UserVocabInstanceCreateRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceFilterRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceUpdateRequest;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for UserVocabInstance operations.
 */
public interface UserVocabInstanceService {

    /**
     * Get all vocabulary instances (paginated)
     */
    Page<UserVocabInstanceResponse> getAll(int page, int size, String sortBy, String sortDirection);

    /**
     * Get vocabulary instance by ID
     */
    UserVocabInstanceResponse getById(UUID id);

    /**
     * Get all vocabulary instances for a user
     */
    List<UserVocabInstanceResponse> getByUserId(UUID userId);

    /**
     * Filter vocabulary instances with criteria
     */
    Page<UserVocabInstanceResponse> filter(UserVocabInstanceFilterRequest filterRequest);

    /**
     * Create a new vocabulary instance
     */
    UserVocabInstanceResponse create(UserVocabInstanceCreateRequest request);

    /**
     * Create multiple vocabulary instances
     */
    List<UserVocabInstanceResponse> createBatch(List<UserVocabInstanceCreateRequest> requests);

    /**
     * Partially update a vocabulary instance
     */
    UserVocabInstanceResponse partialUpdate(UUID id, UserVocabInstanceUpdateRequest request);

    /**
     * Soft delete a vocabulary instance
     */
    void softDelete(UUID id);

    /**
     * Soft delete multiple vocabulary instances
     */
    void softDeleteBatch(List<UUID> ids);
}
