# Employee Management System

A production-ready Spring Boot application for managing employees, featuring secure authentication, role-based access control, auditing, and soft delete capabilities.

## Tech Stack
-   **Java 17**
-   **Spring Boot 3.4.1**
-   **Spring Security & JWT**
-   **Spring Data JPA (Hibernate)**
-   **MySQL / PostgreSQL**
-   **Gradle**
-   **Swagger UI (OpenAPI 3)**

## Security Flow
The application uses JWT (JSON Web Tokens) for stateless authentication.
1.  **Register/Login**: User sends credentials to `/api/v1/auth/*`.
2.  **Token Issuance**: Valid credentials return a JWT access token.
3.  **Token Usage**: Client sends the token in the `Authorization` header (`Bearer <token>`) for subsequent requests.
4.  **Authorization**:
    -   `ROLE_ADMIN`: Full access (Create, Update, Delete).
    -   `ROLE_USER`: Read-only access (Get).
    -   **Audit**: The system tracks `createdBy` and `updatedBy` based on the authenticated user.

## ERD (Entity Relationship Diagram)
-   **User**: `id (UUID)`, `name`, `email`, `password`, `role`, `createdAt`, `updatedAt`
-   **Employee**: `id (UUID)`, `name`, `email`, `position`, `salary`, `department`, `isDeleted`, `createdBy`, `updatedBy`, `createdAt`, `updatedAt`

## Run Instructions

### Prerequisites
-   Java 17+
-   Database (MySQL/PostgreSQL) running on port 3306 (or update `application.properties`)

### Steps
1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    ```
2.  **Configure Database**:
    Update `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
    spring.datasource.username=root
    spring.datasource.password=password
    ```
3.  **Build and Run**:
    ```bash
    ./gradlew bootRun
    ```
4.  **Access Swagger UI**:
    [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Example API Request
**Get All Employees (Search)**
```http
GET /api/employees?keyword=developer&page=0&size=10
Authorization: Bearer <your_jwt_token>
```

**Response**:
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Jane Doe",
      "email": "jane@example.com",
      "position": "Senior Developer",
      "department": "Engineering"
    }
  ],
  "totalElements": 1
}
```
