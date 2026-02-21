package com.e4u.lesson_service.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Join entity for Many-to-Many relationship between DynamicLesson and
 * UserVocabInstance.
 * Stores additional metadata about each vocab item in a lesson.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lesson_item")
public class LessonItem extends BaseEntity {

        @EmbeddedId
        private LessonItemKey id;

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("lessonId")
        @JoinColumn(name = "lesson_id")
        private DynamicLesson lesson;

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("wordInstanceId")
        @JoinColumn(name = "word_instance_id")
        private UserVocabInstance wordInstance;

        @Column(name = "is_mastered_in_lesson")
        @Builder.Default
        private Boolean isMasteredInLesson = false;

        @Column(name = "sequence_order")
        private Integer sequenceOrder;
}
