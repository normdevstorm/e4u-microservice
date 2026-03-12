package com.e4u.learning_service.mapper;

import com.e4u.learning_service.dtos.request.UserProfilingRequest;
import com.e4u.learning_service.dtos.response.UserProfileResponse;
import com.e4u.learning_service.entities.UserProfile;
import org.mapstruct.*;

/**
 * MapStruct mapper for UserProfile entity.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserProfileMapper {

    UserProfileResponse toResponse(UserProfile entity);

    /**
     * Applies profiling fields (occupation, interests, dailyTimeCommitment) to an
     * existing entity.
     * Only non-null fields in the request will be updated.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profileId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "privacyConsent", ignore = true)
    @Mapping(target = "proficiencyBaseline", ignore = true)
    @Mapping(target = "currentProficiency", ignore = true)
    @Mapping(target = "isOnboardingComplete", ignore = true)
    void applyProfiling(@MappingTarget UserProfile entity, UserProfilingRequest request);
}
