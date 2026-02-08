package com.renting.authentication_service.common.constants;

public class ConstantManager {
    public static final String[]  URL_WHITE_LIST = {"/login","/signup", "/refresh-token", "/validate-token", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v1/api-docs/**", "/v3/api-docs/**"};
    public static final String  LOGIN_ENDPOINT = "/login";
    public static final String SHOULD_NOT_FILTER_JWT_PATHS_REGEX = "(/signup|/refresh-token|/validate-token|/swagger-ui/.*|/v1/api-docs|/v1/api-docs/.*|/v3/api-docs|/v3/api-docs/.*|/swagger-ui.html)";
}
