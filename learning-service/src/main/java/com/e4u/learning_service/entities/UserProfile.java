package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

/**
 * Stores onboarding and profiling data for a learner.
 * user_id is a logical (non-FK) reference to the auth-service users table.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", nullable = false, updatable = false)
    private UUID profileId;

    /**
     * Logical FK to auth-service users.user_id — no DB constraint across services
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "occupation")
    private String occupation;

    /** Array of interest tags, e.g. ["travel","business","technology"] */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "interests", columnDefinition = "text[]")
    private List<String> interests;

    /** CEFR level assigned after baseline assessment, e.g. "B1" */
    @Column(name = "proficiency_baseline")
    private String proficiencyBaseline;

    /** Current CEFR level, updated over time by learning progress */
    @Column(name = "current_proficiency")
    private String currentProficiency;

    /** Daily study time goal in minutes (default 15) */
    @Builder.Default
    @Column(name = "daily_time_commitment", nullable = false)
    private Integer dailyTimeCommitment = 15;

    /** Whether the user has accepted the privacy policy */
    @Builder.Default
    @Column(name = "privacy_consent", nullable = false)
    private Boolean privacyConsent = false;

    /** True once all onboarding steps are complete */
    @Builder.Default
    @Column(name = "is_onboarding_complete", nullable = false)
    private Boolean isOnboardingComplete = false;
}
