package jmaster.io.gatewayservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jmaster.io.gatewayservice.client.AuthenticationService;
import jmaster.io.gatewayservice.exception.custom.CustomJwtException;
import jmaster.io.gatewayservice.exception.custom.MissingAuthorizationHeader;
import jmaster.io.gatewayservice.response.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Component("Authentication")
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private AuthenticationService authenticationService;
    private RouteValidator routeValidator;

    @Autowired
    public AuthenticationFilter(@Lazy AuthenticationService authenticationService, RouteValidator routeValidator) {
        super(Config.class);
        this.authenticationService = authenticationService;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new MissingAuthorizationHeader("Missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    Map<String, String> tokenMap = Map.of("token", authHeader);
//                    Map<String, String> response = new ObjectMapper().convertValue(authenticationService.validateToken(tokenMap).getBody(), Map.class);
//                    log.info(response.toString());
//                    boolean isValidToken = Boolean.TRUE.equals(response.get("data"));
                    Boolean isValidToken = authenticationService.validateToken(tokenMap).getBody().getData();
                    if (!isValidToken) {
                        throw new CustomJwtException("Unauthorized access to application");
                    }
                    log.info("Authorized access to application");

                } catch (Exception e) {
                    System.out.println("Invalid access...!");
                    throw new CustomJwtException("Unauthorized access to application");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
        // Put the configuration properties for your filter here
    }
}
