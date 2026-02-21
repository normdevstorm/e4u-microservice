package com.e4u.curriculum_service.repositories.specification;

import com.e4u.curriculum_service.entities.GlobalDictionary;
import com.e4u.curriculum_service.models.request.GlobalDictionaryFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering GlobalDictionary entities.
 */
public class GlobalDictionarySpecification {

    private GlobalDictionarySpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<GlobalDictionary> withFilter(GlobalDictionaryFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by lemma (contains, case-insensitive)
            if (filter.getLemma() != null && !filter.getLemma().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lemma")),
                        "%" + filter.getLemma().toLowerCase() + "%"));
            }

            // Filter by part of speech
            if (filter.getPartOfSpeech() != null && !filter.getPartOfSpeech().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("partOfSpeech"), filter.getPartOfSpeech()));
            }

            // Filter by difficulty level
            if (filter.getDifficultyLevel() != null && !filter.getDifficultyLevel().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("difficultyLevel"), filter.getDifficultyLevel()));
            }

            // Filter by frequency score range
            if (filter.getMinFrequencyScore() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("frequencyScore"),
                        filter.getMinFrequencyScore()));
            }
            if (filter.getMaxFrequencyScore() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("frequencyScore"), filter.getMaxFrequencyScore()));
            }

            // General keyword search (in lemma or definition)
            if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
                String keyword = "%" + filter.getKeyword().toLowerCase() + "%";
                Predicate lemmaMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("lemma")), keyword);
                Predicate definitionMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("definition")),
                        keyword);
                predicates.add(criteriaBuilder.or(lemmaMatch, definitionMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
