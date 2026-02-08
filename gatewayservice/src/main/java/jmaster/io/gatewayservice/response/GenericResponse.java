package jmaster.io.gatewayservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class GenericResponse<T> implements Serializable {
    private boolean success;
    private String message;
    private T data;
}