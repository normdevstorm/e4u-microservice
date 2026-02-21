package com.e4u.lesson_service.repositories.specification;

import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.models.request.UserVocabInstanceFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic filtering of UserVocabInstance entities.
 */
public class UserVocabInstanceSpecification {

    private UserVocabInstanceSpecification() {
        // Utility class
    }

    public static Specification<UserVocabInstance> withFilter(UserVocabInstanceFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), filter.getUserId()));
            }

            if (filter.getWordText() != null && !filter.getWordText().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("wordText")),
                        "%" + filter.getWordText().toLowerCase() + "%"
                ));
            }

            if (filter.getIsJobMatch() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isJobMatch"), filter.getIsJobMatch()));
            }

            if (filter.getIsGoalMatch() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isGoalMatch"), filter.getIsGoalMatch()));
            }

            if (filter.getIsLearning() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isLearning"), filter.getIsLearning()));
            }

            if (filter.getIsMastered() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isMastered"), filter.getIsMastered()));
            }

            if (filter.getSourceType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sourceType"), filter.getSourceType()));
            }

            if (filter.getMinRelevanceScore() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("relevanceScore"), filter.getMinRelevanceScore()
                ));
            }

            if (filter.getMaxRelevanceScore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("relevanceScore"), filter.getMaxRelevanceScore()
                ));
            }

            if (filter.getMinFrequencyCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("frequencyCount"), filter.getMinFrequencyCount()
                ));
            }

            if (filter.getMaxFrequencyCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("frequencyCount"), filter.getMaxFrequencyCount()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
