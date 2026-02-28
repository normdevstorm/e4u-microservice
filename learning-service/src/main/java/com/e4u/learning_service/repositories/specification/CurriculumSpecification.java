package com.e4u.learning_service.repositories.specification;

import com.e4u.learning_service.dtos.request.CurriculumFilterRequest;
import com.e4u.learning_service.entities.Curriculum;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering Curriculum entities.
 */
public class CurriculumSpecification {

    private CurriculumSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<Curriculum> withFilter(CurriculumFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by curriculum name (contains, case-insensitive)
            if (filter.getCurriculumName() != null && !filter.getCurriculumName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("curriculumName")),
                        "%" + filter.getCurriculumName().toLowerCase() + "%"));
            }

            // Filter by goal ID
            if (filter.getGoalId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("goalDefinition").get("goalId"), filter.getGoalId()));
            }

            // Filter by isActive
            if (filter.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), filter.getIsActive()));
            }

            // Filter by target goals (contains, case-insensitive)
            if (filter.getTargetGoalsContains() != null && !filter.getTargetGoalsContains().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("targetGoals")),
                        "%" + filter.getTargetGoalsContains().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
