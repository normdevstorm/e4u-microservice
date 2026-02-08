package jmaster.io.gatewayservice.client;

import jmaster.io.gatewayservice.response.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Override
    public ResponseEntity<GenericResponse<Boolean>> validateToken(Map<String, String> token) {
        log.info("Authentication Service is having issues.");
        return ResponseEntity.status(503).body(GenericResponse.<Boolean>builder().data(false).success(false).message("Authorization failed !!!").build());
    }
}