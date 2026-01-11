package com.rfrmd.employeemanagement.e2e;

import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.Role;
import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityRegressionTest extends AbstractE2ETest {

    private String userToken;
    private String adminToken;

    @org.junit.jupiter.api.BeforeEach
    void setupUsers() {
        if (adminToken != null)
            return;

        // Register Admin
        adminToken = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("Sec Admin", "sec_admin@test.com", "123456", Role.ADMIN))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract().path("access_token");

        // Register User
        userToken = given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("Sec User", "sec_user@test.com", "123456", Role.USER))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract().path("access_token");
    }

    @Test
    void user_CannotCreateEmployee() {
        EmployeeDto emp = EmployeeDto.builder()
                .name("Hacker")
                .email("hacker@test.com")
                .salary(BigDecimal.TEN)
                .build();

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(emp)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(403); // Forbidden
    }

    @Test
    void unauthenticated_CannotAccessEndpoints() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/employees")
                .then()
                .statusCode(403); // Or 401
    }

    @Test
    void error_ShouldNotLeakStackTraces() {
        // Trigger a 404
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees/" + java.util.UUID.randomUUID())
                .then()
                .statusCode(404)
                .body("message", not(containsString("com.rfrmd"))); // No package names

        // Trigger a Bad Request (Invalid UUID)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees/invalid-uuid")
                .then()
                .statusCode(400) // Or 500 depending on handling
                .body("message", not(containsString("java.lang"))); // No class names
    }
}
