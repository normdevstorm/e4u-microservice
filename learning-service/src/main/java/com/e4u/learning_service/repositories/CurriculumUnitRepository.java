package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.CurriculumUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurriculumUnitRepository
        extends JpaRepository<CurriculumUnit, UUID>, JpaSpecificationExecutor<CurriculumUnit> {

    // Find by ID (non-deleted)
    Optional<CurriculumUnit> findByIdAndDeletedFalse(UUID id);

    // Find all (non-deleted)
    Page<CurriculumUnit> findByDeletedFalse(Pageable pageable);

    // Find by curriculum ID (non-deleted, ordered)
    List<CurriculumUnit> findByCurriculum_IdAndDeletedFalseOrderByDefaultOrderAsc(UUID curriculumId);

    // Find by curriculum ID with pagination
    Page<CurriculumUnit> findByCurriculum_IdAndDeletedFalse(UUID curriculumId, Pageable pageable);

    // Find with base words
    @Query("SELECT DISTINCT u FROM CurriculumUnit u LEFT JOIN FETCH u.baseWords bw LEFT JOIN FETCH bw.word WHERE u.id = :id AND u.deleted = false")
    Optional<CurriculumUnit> findByIdWithBaseWords(@Param("id") UUID id);

    // Find by proficiency level
    List<CurriculumUnit> findByRequiredProficiencyLevelAndDeletedFalse(String proficiencyLevel);

    // Soft delete
    @Modifying
    @Query("UPDATE CurriculumUnit u SET u.deleted = true, u.deletedAt = :deletedAt WHERE u.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

    // Soft delete all units in a curriculum
    @Modifying
    @Query("UPDATE CurriculumUnit u SET u.deleted = true, u.deletedAt = :deletedAt WHERE u.curriculum.id = :curriculumId")
    int softDeleteByCurriculumId(@Param("curriculumId") UUID curriculumId, @Param("deletedAt") Instant deletedAt);

    // Get max order in a curriculum
    @Query("SELECT COALESCE(MAX(u.defaultOrder), 0) FROM CurriculumUnit u WHERE u.curriculum.id = :curriculumId AND u.deleted = false")
    Integer findMaxOrderByCurriculumId(@Param("curriculumId") UUID curriculumId);

    // Check if unit name exists in curriculum
    boolean existsByUnitNameAndCurriculum_IdAndDeletedFalse(String unitName, UUID curriculumId);
}
