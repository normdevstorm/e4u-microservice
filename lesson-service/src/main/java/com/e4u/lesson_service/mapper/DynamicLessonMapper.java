package com.e4u.lesson_service.mapper;

import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.entities.LessonItem;
import com.e4u.lesson_service.entities.UserUnitState;
import com.e4u.lesson_service.models.response.DynamicLessonDetailResponse;
import com.e4u.lesson_service.models.response.DynamicLessonResponse;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between DynamicLesson entity and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserVocabInstanceMapper.class })
public interface DynamicLessonMapper {

    /**
     * Convert entity to response DTO (lightweight, only vocab IDs)
     */
    @Mapping(target = "userUnitStateId", expression = "java(mapUserUnitStateId(entity.getUserUnitState()))")
    @Mapping(target = "vocabItemIds", expression = "java(mapLessonItemsToVocabIds(entity.getLessonItems()))")
    DynamicLessonResponse toResponse(DynamicLesson entity);

    /**
     * Convert list of entities to list of response DTOs
     */
    List<DynamicLessonResponse> toResponseList(List<DynamicLesson> entities);

    /**
     * Convert entity to detailed response DTO (includes full vocab items)
     */
    @Mapping(target = "userUnitStateId", expression = "java(mapUserUnitStateId(entity.getUserUnitState()))")
    @Mapping(target = "vocabItems", expression = "java(mapLessonItemsToVocabResponses(entity.getLessonItems(), vocabMapper))")
    DynamicLessonDetailResponse toDetailResponse(DynamicLesson entity, @Context UserVocabInstanceMapper vocabMapper);

    /**
     * Helper method to safely extract UserUnitState ID
     */
    default UUID mapUserUnitStateId(UserUnitState userUnitState) {
        return userUnitState != null ? userUnitState.getId() : null;
    }

    /**
     * Helper method to map lesson items to vocab instance IDs
     */
    default Set<UUID> mapLessonItemsToVocabIds(Set<LessonItem> lessonItems) {
        if (lessonItems == null) {
            return null;
        }
        return lessonItems.stream()
                .filter(item -> item.getWordInstance() != null)
                .map(item -> item.getWordInstance().getId())
                .collect(Collectors.toSet());
    }

    /**
     * Helper method to map lesson items to vocab responses
     */
    default List<UserVocabInstanceResponse> mapLessonItemsToVocabResponses(
            Set<LessonItem> lessonItems,
            UserVocabInstanceMapper vocabMapper) {
        if (lessonItems == null || vocabMapper == null) {
            return null;
        }
        return lessonItems.stream()
                .filter(item -> item.getWordInstance() != null)
                .map(item -> vocabMapper.toResponse(item.getWordInstance()))
                .collect(Collectors.toList());
    }
}
