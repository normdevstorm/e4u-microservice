package com.renting.authentication_service.common.exception.handler;
import com.renting.authentication_service.common.exception.custom.CustomEntityNotFoundException;
import com.renting.authentication_service.common.exception.custom.CustomJwtException;
import com.renting.authentication_service.response.GenericException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

@RestController
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<GenericException> customThrowException(UsernameNotFoundException e) {
        GenericException exceptionGenericResponse = GenericException.builder().timestamp(new Date()).status(HttpStatus.NOT_FOUND).details(e.getCause().toString()).message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionGenericResponse);
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<GenericException> badCredentialsExceptionHandler(BadCredentialsException e) {
        GenericException exceptionGenericResponse = GenericException.builder().timestamp(new Date()).status(HttpStatus.BAD_REQUEST).details("error").message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(exceptionGenericResponse);
    }

    @ExceptionHandler(value = {MalformedJwtException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GenericException> malformedJwtExceptionHandler(MalformedJwtException e) {
        GenericException exceptionGenericResponse = GenericException.builder().timestamp(new Date()).status(HttpStatus.UNAUTHORIZED).details("error").message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(exceptionGenericResponse);
    }

    @ExceptionHandler(value = {NoSuchAlgorithmException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GenericException> noSuchAlgorithmException(NoSuchAlgorithmException e) {
        GenericException exceptionGenericResponse = GenericException.builder().timestamp(new Date()).status(HttpStatus.INTERNAL_SERVER_ERROR).details("error").message(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(exceptionGenericResponse);
    }

    @ExceptionHandler(value = {CustomJwtException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GenericException> handleCustomJwtException(CustomJwtException e) {
        GenericException genericResponse = GenericException.builder().timestamp(new Date()).status(HttpStatus.UNAUTHORIZED).message(e.getMessage()).details(e.getMessage()).build();
        if (e.getCause() != null) {
            log.warn(e.getCause().getMessage());
        }
        return ResponseEntity.status(genericResponse.getStatus()).body(genericResponse);
    }

    @ExceptionHandler(value = {AuthorizationDeniedException.class})
    public ResponseEntity<GenericException> authorizationDeniedException(AuthorizationDeniedException e) {
        GenericException genericResponse = GenericException.builder().status(HttpStatus.FORBIDDEN).message("FORBIDDEN").details(e.getMessage()).timestamp(Date.from(Instant.now())).build();
        return ResponseEntity.status(genericResponse.getStatus()).body(genericResponse);
    }

    @ExceptionHandler(value = {CustomEntityNotFoundException.class})
    public ResponseEntity<GenericException> entityNotFoundExceptionHandler(CustomEntityNotFoundException e) {
        GenericException genericResponse = GenericException.builder().status(HttpStatus.NOT_FOUND).message("NOT_FOUND").details(e.getMessage()).timestamp(Date.from(Instant.now())).build();
        return ResponseEntity.status(genericResponse.getStatus()).body(genericResponse);
    }


//    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
//    public ResponseEntity<GenericException> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
//        GenericException genericResponse = GenericException.builder().status(HttpStatus.BAD_REQUEST).message("BAD_REQUEST").details(e.getMessage()).timestamp(Date.from(Instant.now())).build();
//        return ResponseEntity.status(genericResponse.getStatus()).body(genericResponse);
//    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        StringBuilder invalidMessage = new StringBuilder();
        ((BeanPropertyBindingResult) ex.getBindingResult()).getAllErrors().forEach(error -> {
            invalidMessage.append(System.lineSeparator());
            invalidMessage.append( error.getDefaultMessage());
        });
        GenericException genericResponse = GenericException.builder().status(HttpStatus.BAD_REQUEST).message("BAD_REQUEST").details(invalidMessage.toString()).timestamp(Date.from(Instant.now())).build();
        return ResponseEntity.status(genericResponse.getStatus()).body(genericResponse);
    }
}
