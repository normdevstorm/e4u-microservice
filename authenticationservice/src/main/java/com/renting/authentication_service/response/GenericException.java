package com.renting.authentication_service.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@Builder
public class GenericException {
    private Date timestamp;
    private String message;
    private  String details;
    private HttpStatus status;
}
