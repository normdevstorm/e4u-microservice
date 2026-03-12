package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Reference data for onboarding interest-tag options.
 * Loaded from DB and served via GET /v1/onboarding/options/interests.
 * No audit fields needed — admin-managed static content.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "onboarding_interests")
public class OnboardingInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Machine-readable key, e.g. "travel", "technology" */
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /** Human-readable label shown in UI, e.g. "Travel", "Technology" */
    @Column(name = "label", nullable = false)
    private String label;

    /** Ordering hint for UI display */
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
