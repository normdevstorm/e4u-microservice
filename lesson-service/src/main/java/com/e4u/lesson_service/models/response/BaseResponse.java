package com.e4u.lesson_service.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper for all successful responses.
 *
 * @param <T> The type of data being returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    @Builder.Default
    private boolean success = true;

    private T data;

    private String message;

    /**
     * Create a successful response with data
     */
    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Create a successful response with data and message
     */
    public static <T> BaseResponse<T> ok(T data, String message) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    /**
     * Create a successful response with message only
     */
    public static <T> BaseResponse<T> ok(String message) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * Create an empty successful response
     */
    public static <T> BaseResponse<T> ok() {
        return BaseResponse.<T>builder()
                .success(true)
                .build();
    }
}
