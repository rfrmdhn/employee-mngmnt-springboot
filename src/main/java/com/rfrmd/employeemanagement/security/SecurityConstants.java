package com.rfrmd.employeemanagement.security;

public class SecurityConstants {
    // Endpoints
    public static final String AUTH_WHITELIST = "/api/auth/**";
    public static final String API_DOCS = "/v3/api-docs/**";
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String EMPLOYEES_ENDPOINT = "/api/employees/**";
    public static final String BEARER_PREFIX = "Bearer ";

    // Roles
    // Role names are already handled by Role.name(), but we can add more if needed

    private SecurityConstants() {
        // Private constructor to hide the implicit public one
    }
}
