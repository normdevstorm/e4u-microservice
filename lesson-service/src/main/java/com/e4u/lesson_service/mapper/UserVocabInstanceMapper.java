package com.e4u.lesson_service.mapper;

import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.models.request.UserVocabInstanceCreateRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceUpdateRequest;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for converting between UserVocabInstance entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserVocabInstanceMapper {

    /**
     * Convert entity to response DTO
     */
    UserVocabInstanceResponse toResponse(UserVocabInstance entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    List<UserVocabInstanceResponse> toResponseList(List<UserVocabInstance> entities);

    /**
     * Convert create request DTO to entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "exercises", ignore = true)
    @Mapping(target = "assets", ignore = true)
    @Mapping(target = "isLearning", defaultValue = "true")
    @Mapping(target = "isMastered", defaultValue = "false")
    UserVocabInstance toEntity(UserVocabInstanceCreateRequest request);

    /**
     * Convert list of create requests to list of entities
     */
    List<UserVocabInstance> toEntityList(List<UserVocabInstanceCreateRequest> requests);

    /**
     * Partially update entity from update request DTO.
     * Only non-null fields in the request will update the entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "exercises", ignore = true)
    @Mapping(target = "assets", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget UserVocabInstance entity, UserVocabInstanceUpdateRequest request);
}