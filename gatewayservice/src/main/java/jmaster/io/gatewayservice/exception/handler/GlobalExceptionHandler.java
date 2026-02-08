package jmaster.io.gatewayservice.exception.handler;
import jmaster.io.gatewayservice.exception.custom.CustomJwtException;
import jmaster.io.gatewayservice.exception.custom.MissingAuthorizationHeader;
import jmaster.io.gatewayservice.response.GenericException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(value = {MissingAuthorizationHeader.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<GenericException> handleMissingAuthorizationHeaderException(MissingAuthorizationHeader e) {
        GenericException genericResponse = GenericException.builder().timestamp(new Date()).status(HttpStatus.UNAUTHORIZED).message(e.getMessage()).details(e.getMessage()).build();
//        if (e.getCause() != null) {
//            log.warn(e.getCause().getMessage());
//        }
        return ResponseEntity.status(genericResponse.getStatus()).body(genericResponse);
    }
}
