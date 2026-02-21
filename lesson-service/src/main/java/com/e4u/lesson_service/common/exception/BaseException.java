package com.e4u.lesson_service.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standard API error response model.
 * Used for all error responses: success=false, code, message, detail.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseException {

    @Builder.Default
    private boolean success = false;

    private String code;

    private String message;

    private String detail;

    private List<FieldError> errors;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String path;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

    public static BaseException of(String code, String message) {
        return BaseException.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static BaseException of(String code, String message, String detail) {
        return BaseException.builder()
                .code(code)
                .message(message)
                .detail(detail)
                .build();
    }

    public static BaseException of(String code, String message, List<FieldError> errors) {
        return BaseException.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}
