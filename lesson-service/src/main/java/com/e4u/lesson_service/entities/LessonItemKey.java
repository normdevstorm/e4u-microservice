package com.e4u.lesson_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonItemKey implements Serializable {

    @Column(name = "lesson_id", nullable = false)
    private UUID lessonId;

    @Column(name = "word_instance_id", nullable = false)
    private UUID wordInstanceId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LessonItemKey that = (LessonItemKey) o;
        return Objects.equals(lessonId, that.lessonId) &&
                Objects.equals(wordInstanceId, that.wordInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId, wordInstanceId);
    }
}
