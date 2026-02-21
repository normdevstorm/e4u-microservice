package com.e4u.lesson_service.repositories.specification;

import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.models.request.LessonExerciseFilterRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering LessonExercise entities.
 */
public class LessonExerciseSpecification {

    private LessonExerciseSpecification() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a specification based on the filter request.
     * Automatically excludes soft-deleted records.
     */
    public static Specification<LessonExercise> withFilter(LessonExerciseFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by lessonId
            if (filter.getLessonId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("lesson").get("id"), filter.getLessonId()));
            }

            // Filter by wordInstanceId
            if (filter.getWordInstanceId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("wordInstance").get("id"), filter.getWordInstanceId()));
            }

            // Filter by unitId (via lesson -> userUnitState)
            if (filter.getUnitId() != null) {
                Join<?, ?> lessonJoin = root.join("lesson", JoinType.INNER);
                Join<?, ?> unitJoin = lessonJoin.join("userUnitState", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(unitJoin.get("id"), filter.getUnitId()));
            }

            // Filter by exerciseType
            if (filter.getExerciseType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("exerciseType"), filter.getExerciseType()));
            }

            // Filter by isCompleted
            if (filter.getIsCompleted() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCompleted"), filter.getIsCompleted()));
            }

            // Filter by isCorrect
            if (filter.getIsCorrect() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCorrect"), filter.getIsCorrect()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
