package com.e4u.curriculum_service.repositories.specification;

import com.e4u.curriculum_service.entities.GoalDefinition;
import com.e4u.curriculum_service.models.request.GoalDefinitionFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering GoalDefinition entities.
 */
public class GoalDefinitionSpecification {

    private GoalDefinitionSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<GoalDefinition> withFilter(GoalDefinitionFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by goal name (contains, case-insensitive)
            if (filter.getGoalName() != null && !filter.getGoalName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("goalName")),
                        "%" + filter.getGoalName().toLowerCase() + "%"));
            }

            // Filter by isActive
            if (filter.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), filter.getIsActive()));
            }

            // Filter by skills focus (contains any skill)
            if (filter.getSkillsFocusContains() != null && !filter.getSkillsFocusContains().isBlank()) {
                // PostgreSQL array contains check using native query function
                predicates.add(criteriaBuilder.isTrue(
                        criteriaBuilder.function(
                                "array_to_string",
                                String.class,
                                root.get("skillsFocus"),
                                criteriaBuilder.literal(",")).in(filter.getSkillsFocusContains())));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
