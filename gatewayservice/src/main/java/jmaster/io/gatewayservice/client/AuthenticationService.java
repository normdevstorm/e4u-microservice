package jmaster.io.gatewayservice.client;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jmaster.io.gatewayservice.response.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "authentication-service", url = "http://localhost:8082", fallback = AuthenticationServiceImpl.class )
public interface AuthenticationService {
    @PostMapping(value = "/validate-token", produces = "application/json", headers = {
            "Content-Type=application/json" })
    ResponseEntity<GenericResponse<Boolean>> validateToken(@RequestBody Map<String, String> token);
}

