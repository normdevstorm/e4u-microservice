package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.LessonItem;
import com.e4u.lesson_service.entities.LessonItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonItemRepository extends JpaRepository<LessonItem, LessonItemKey> {

    List<LessonItem> findByIdLessonIdAndDeletedFalse(UUID lessonId);

    List<LessonItem> findByIdWordInstanceIdAndDeletedFalse(UUID wordInstanceId);

    @Query("SELECT li FROM LessonItem li JOIN FETCH li.wordInstance WHERE li.id.lessonId = :lessonId AND li.deleted = false ORDER BY li.sequenceOrder")
    List<LessonItem> findByLessonIdWithVocabInstances(@Param("lessonId") UUID lessonId);

    void deleteByIdLessonId(UUID lessonId);

    boolean existsByIdLessonIdAndIdWordInstanceId(UUID lessonId, UUID wordInstanceId);
}
