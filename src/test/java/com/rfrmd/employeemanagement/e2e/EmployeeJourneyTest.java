package com.rfrmd.employeemanagement.e2e;

import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.Role;
import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeJourneyTest extends AbstractE2ETest {

    private static String adminToken;
    private static UUID createdEmployeeId;

    @Test
    @Order(1)
    void setupAdmin() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Admin User",
                "admin@e2e.com",
                "admin123",
                Role.ADMIN);

        adminToken = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract().path("access_token");
    }

    @Test
    @Order(2)
    void createEmployee() {
        EmployeeDto newEmployee = EmployeeDto.builder()
                .name("Alice E2E")
                .email("alice@e2e.com")
                .position("Tester")
                .department("QA")
                .salary(BigDecimal.valueOf(60000))
                .build();

        String idStr = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(newEmployee)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(200)
                .body("name", equalTo("Alice E2E"))
                .extract().path("id");

        createdEmployeeId = UUID.fromString(idStr);
    }

    @Test
    @Order(3)
    void getEmployee() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees/" + createdEmployeeId)
                .then()
                .statusCode(200)
                .body("email", equalTo("alice@e2e.com"));
    }

    @Test
    @Order(4)
    void updateEmployee() {
        EmployeeDto update = EmployeeDto.builder()
                .name("Alice Updated")
                .email("alice@e2e.com") // same email
                .position("Senior Tester")
                .department("QA")
                .salary(BigDecimal.valueOf(65000))
                .build();

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put("/api/employees/" + createdEmployeeId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Alice Updated"))
                .body("salary", equalTo(65000));
    }

    @Test
    @Order(5)
    void deleteEmployee_SoftDeleteCheck() {
        // Delete
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete("/api/employees/" + createdEmployeeId)
                .then()
                .statusCode(204);

        // Verify Fetch returns 404 (due to @SQLRestriction)
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees/" + createdEmployeeId)
                .then()
                .statusCode(404);

        // Verify List doesn't contain it
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/employees")
                .then()
                .statusCode(200)
                .body("content.id", not(hasItem(createdEmployeeId.toString())));
    }
}
