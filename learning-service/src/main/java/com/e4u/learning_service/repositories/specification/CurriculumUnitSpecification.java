package com.e4u.learning_service.repositories.specification;

import com.e4u.learning_service.dtos.request.CurriculumUnitFilterRequest;
import com.e4u.learning_service.entities.CurriculumUnit;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering CurriculumUnit entities.
 */
public class CurriculumUnitSpecification {

    private CurriculumUnitSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<CurriculumUnit> withFilter(CurriculumUnitFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by curriculum ID
            if (filter.getCurriculumId() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("curriculum").get("curriculumId"), filter.getCurriculumId()));
            }

            // Filter by unit name (contains, case-insensitive)
            if (filter.getUnitName() != null && !filter.getUnitName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("unitName")),
                        "%" + filter.getUnitName().toLowerCase() + "%"));
            }

            // Filter by required proficiency level
            if (filter.getRequiredProficiencyLevel() != null && !filter.getRequiredProficiencyLevel().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("requiredProficiencyLevel"),
                        filter.getRequiredProficiencyLevel()));
            }

            // Filter by isActive
            if (filter.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), filter.getIsActive()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
