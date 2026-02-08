package com.renting.item_service.client;

import org.slf4j.Logger;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@FeignClient(name = "authentication-service", url = "http://localhost:8082", fallback = AuthenticationServiceImpl.class)
public interface AuthenticationService {

    @PostMapping(value = "validate-token", produces = "application/json", headers = {
            "Content-Type=application/json" })
    ResponseEntity<?> validateToken(String token);
}

class AuthenticationServiceImpl implements AuthenticationService {

    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Override
    public ResponseEntity<?> validateToken(String token) {
        // fallback
        logger.error("Authentication Service is slow");
        return ResponseEntity.status(503).body("Authentication Service is unavailable");
    }
}