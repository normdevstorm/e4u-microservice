package com.e4u.curriculum_service.repositories.specification;

import com.e4u.curriculum_service.entities.TranslationDict;
import com.e4u.curriculum_service.models.request.TranslationDictFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering TranslationDict entities.
 */
public class TranslationDictSpecification {

    private TranslationDictSpecification() {
        // Private constructor to prevent instantiation
    }

    public static Specification<TranslationDict> withFilter(TranslationDictFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (filter == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // Filter by word ID
            if (filter.getWordId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("word").get("wordId"), filter.getWordId()));
            }

            // Filter by destination language
            if (filter.getDestLang() != null && !filter.getDestLang().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("destLang"), filter.getDestLang()));
            }

            // Filter by translation keyword (contains, case-insensitive)
            if (filter.getTranslationKeyword() != null && !filter.getTranslationKeyword().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("translation")),
                        "%" + filter.getTranslationKeyword().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
