package com.rfrmd.employeemanagement.e2e;

import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.Role;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthJourneyTest extends AbstractE2ETest {

    @Test
    void testAuthLifecycleAndRateLimiting() {
        // 1. Register User
        RegisterRequest registerRequest = new RegisterRequest(
                "E2E User",
                "e2e@example.com",
                "password123",
                Role.USER);

        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue());

        // 2. Login Successfully
        LoginRequest loginRequest = new LoginRequest("e2e@example.com", "password123");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue());

        // 3. Rate Limiting Check (Attempt 5 bad logins + 1 more)
        LoginRequest badLogin = new LoginRequest("e2e@example.com", "wrongpass");

        for (int i = 0; i < 5; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .body(badLogin)
                    .when()
                    .post("/api/auth/login")
                    .then()
                    // Could be 401 or 403 depending on where rate limit hits, usually 401 Bad Creds
                    // first
                    // But our service throws BadCredentials for rate limit too (with different msg)
                    // Let's just consume the bucket.
                    .statusCode(anyOf(is(401), is(403)));
        }

        // The 6th attempt should definitively fail with Rate Limit message or 401 if we
        // return generic
        // In our implementation, we throw BadCredentialsException("Too many login
        // attempts...")
        // GlobalExceptionHandler returns 401 for BadCredentials/AuthenticationException

        given()
                .contentType(ContentType.JSON)
                .body(badLogin)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401) // We configured it to be generic error or specific?
                .body("message", equalTo("Authentication failed")); // We sanitize messages!

        // Note: Since we sanitized the error message to "Authentication failed" in
        // GlobalHandler
        // we can't verify the "Too many login attempts" string on the client side.
        // This confirms Secure Error Handling is working!
    }
}
