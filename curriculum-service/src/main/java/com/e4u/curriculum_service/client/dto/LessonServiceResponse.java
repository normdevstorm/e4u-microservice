package com.e4u.curriculum_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper for lesson-service responses.
 * 
 * @param <T> The type of data in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonServiceResponse<T> {

    private boolean success;
    private T data;
    private String message;
}
