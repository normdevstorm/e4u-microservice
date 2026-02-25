package com.e4u.lesson_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper for curriculum-service responses.
 * 
 * @param <T> The type of data in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumServiceResponse<T> {

    private boolean success;
    private T data;
    private String message;
}
