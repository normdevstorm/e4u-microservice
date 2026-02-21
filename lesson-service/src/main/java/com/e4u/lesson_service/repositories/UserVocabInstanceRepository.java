package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.UserVocabInstance;
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
public interface UserVocabInstanceRepository extends JpaRepository<UserVocabInstance, UUID>, JpaSpecificationExecutor<UserVocabInstance> {
    
    // Find by user
    List<UserVocabInstance> findByUserIdAndDeletedFalse(UUID userId);
    Page<UserVocabInstance> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);
    
    // Find by user and learning status
    List<UserVocabInstance> findByUserIdAndIsLearningAndDeletedFalse(UUID userId, Boolean isLearning);
    List<UserVocabInstance> findByUserIdAndIsMasteredAndDeletedFalse(UUID userId, Boolean isMastered);
    
    // Find by ID and not deleted
    Optional<UserVocabInstance> findByIdAndDeletedFalse(UUID id);
    
    // Check existence
    boolean existsByUserIdAndWordTextAndDeletedFalse(UUID userId, String wordText);
    
    // Soft delete
    @Modifying
    @Query("UPDATE UserVocabInstance v SET v.deleted = true, v.deletedAt = :deletedAt WHERE v.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);
    
    // Soft delete multiple
    @Modifying
    @Query("UPDATE UserVocabInstance v SET v.deleted = true, v.deletedAt = :deletedAt WHERE v.id IN :ids")
    int softDeleteByIds(@Param("ids") List<UUID> ids, @Param("deletedAt") Instant deletedAt);
    
    // Count by user
    long countByUserIdAndDeletedFalse(UUID userId);
    long countByUserIdAndIsLearningAndDeletedFalse(UUID userId, Boolean isLearning);
    long countByUserIdAndIsMasteredAndDeletedFalse(UUID userId, Boolean isMastered);
}
