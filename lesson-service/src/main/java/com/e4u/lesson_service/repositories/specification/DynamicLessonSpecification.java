package com.e4u.lesson_service.repositories.specification;

import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.models.request.DynamicLessonFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering DynamicLesson entities.
 */
public class DynamicLessonSpecification {

    private DynamicLessonSpecification() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a specification based on the filter request.
     * Automatically excludes soft-deleted records.
     */
    public static Specification<DynamicLesson> withFilter(DynamicLessonFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by userId
            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), filter.getUserId()));
            }

            // Filter by userUnitStateId
            if (filter.getUserUnitStateId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("userUnitState").get("id"),
                        filter.getUserUnitStateId()));
            }

            // Filter by status
            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            // Filter by accuracy rate range
            if (filter.getMinAccuracyRate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("accuracyRate"),
                        filter.getMinAccuracyRate()));
            }
            if (filter.getMaxAccuracyRate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("accuracyRate"),
                        filter.getMaxAccuracyRate()));
            }

            // Filter by started date range
            if (filter.getStartedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startedAt"),
                        filter.getStartedAfter()));
            }
            if (filter.getStartedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("startedAt"),
                        filter.getStartedBefore()));
            }

            // Filter by completed date range
            if (filter.getCompletedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("completedAt"),
                        filter.getCompletedAfter()));
            }
            if (filter.getCompletedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("completedAt"),
                        filter.getCompletedBefore()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
