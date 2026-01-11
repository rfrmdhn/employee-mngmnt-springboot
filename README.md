# Employee Management System (SpringBoot Portfolio)

**Robust, Secure, and Scalable Backend API for Managing Employee Records.**

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Build Status](https://img.shields.io/badge/Build-Success-brightgreen)
![Test Coverage](https://img.shields.io/badge/Tests-100%25-brightgreen)

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture & Methodology](#architecture--methodology)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Employee Management](#employee-management)
- [Testing](#testing)
- [Security](#security)
- [Future Improvements](#future-improvements)

---

## Overview

The **Employee Management System** is a professional-grade backend REST API designed to demonstrate best practices in modern Java development. It provides a secure and efficient way to manage employee data, featuring strict authentication, role-based access control (RBAC), and comprehensive validation.

Unlike simple CRUD applications, this project emphasizes **production-readiness**, including rate limiting to prevent brute-force attacks, automated DTO mapping, soft deletes, and a complete end-to-end test suite.

---

## Key Features

- **Secure Authentication**: Stateless JWT (JSON Web Token) authentication with Refresh Token support.
- **Role-Based Access Control (RBAC)**: Fine-grained permissions for `ADMIN` vs `USER` roles.
- **Rate Limiting**: Built-in protection against brute-force login attempts using **Bucket4j**.
- **Advanced Search**: Generic JPA Specifications for dynamic filtering and pagination.
- **Soft Deletes**: Automated handling of deleted records (data preservation) via Hibernate's `@SQLRestriction`.
- **Automated Mapping**: Fast and type-safe DTO-Entity conversion using **MapStruct**.
- **API Documentation**: Integrated Swagger UI / OpenAPI 3.0.
- **Comprehensive Testing**: 100% pass rate for Unit and End-to-End (E2E) tests using **RestAssured** and **H2**.

---

## Tech Stack

| Category | Technology | Purpose |
| :--- | :--- | :--- |
| **Language** | Java 17 | Core programming language |
| **Framework** | Spring Boot 3.4.1 | Web, DI, and Security framework |
| **Database** | MySQL 8.0 | Primary relational database |
| **Testing DB** | H2 Database | In-memory database for E2E testing |
| **ORM** | Spring Data JPA (Hibernate) | Database interaction and abstractions |
| **Security** | Spring Security 6 / JWT | Auth & Authorization |
| **Rate Limiting** | Bucket4j | API Throttling & Protection |
| **Mapping** | MapStruct | Object-to-Object mapping |
| **Testing** | JUnit 5, Mockito, RestAssured | Unit & Integration testing |
| **Build Tool** | Gradle | Dependency management and build automation |

---

## Architecture & Methodology

This project follows a **Modular Monolith** architecture with a clear separation of concerns, adhering to **SOLID Principles**.

### Layered Design
1.  **Controller Layer**: Handles HTTP requests, proper error handling, and simple input validation.
2.  **Service Layer**: Contains business logic, including rate limiting and helper calls. Uses `MapStruct` for DTO conversions.
3.  **Repository Layer**: Interacts with the database using Spring Data JPA. Includes custom `Specifications` for search.
4.  **Database**: MySQL with normalized schema.

### Design Decisions
-   **Soft Deletes**: Implemented at the Entity level (`@SQLRestriction("is_deleted = false")`) ensuring accidentally deleted data can be recovered and preventing data integrity issues.
-   **Security First**: Sensitive fields (like passwords) are never returned in responses. Error messages are sanitized to prevent information leaks.
-   **Environment Variables**: All secrets (DB credentials, JWT keys) are managed via `.env` files, ensuring no sensitive data is hardcoded (The 12-Factor App).

---

## Getting Started

### Prerequisites
-   **Java 17 Development Kit (JDK)**
-   **MySQL Server** (running locally or via Docker)
-   **Gradle** (wrapper included)

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/rfrmdhn/employee-mngmnt-springboot.git
    cd employee-mngmnt-springboot
    ```

2.  **Configure Environment Variables**
    Duplicate the `.env.example` file and rename it to `.env`:
    ```bash
    cp .env.example .env
    ```
    Then update the values in `.env`:
    ```env
    DB_USERNAME=root
    DB_PASSWORD=your_password
    JWT_SECRET_KEY=your_very_long_and_secure_secret_key_at_least_256_bits
    ```

### Configuration
The application uses `src/main/resources/application.yml` for core settings. It is pre-configured to read from your `.env` file.

### Running the Application

1.  **Build the project**
    ```bash
    ./gradlew clean build
    ```

2.  **Run the application**
    ```bash
    ./gradlew bootRun
    ```

The server will start on `http://localhost:8080`.

---

## API Documentation

Once the application is running, you can access the interactive Swagger UI documentation at:

**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### Key Endpoints

#### Authentication
| Method | Endpoint | Description | Public? |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user (`ADMIN` or `USER`) | Yes |
| `POST` | `/api/auth/login` | Login and receive Access Token | Yes |

#### Employee Management
| Method | Endpoint | Description | Role Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/employees` | List all employees (Pagination + Search) | `USER`, `ADMIN` |
| `GET` | `/api/employees/{id}` | Get specific employee details | `USER`, `ADMIN` |
| `POST` | `/api/employees` | Create a new employee | `ADMIN` |
| `PUT` | `/api/employees/{id}` | Update existing employee info | `ADMIN` |
| `DELETE` | `/api/employees/{id}` | Soft delete an employee | `ADMIN` |

---

## Testing

This project maintains a **10/10 Code Quality & Reliability** standard. It includes a complete suite of tests.

### Running Tests
To run all unit and end-to-end tests:
```bash
./gradlew test
```

### Test Strategy
1.  **Unit Tests**: Focus on isolated business logic.
    -   *Tools*: JUnit 5, Mockito.
    -   *Coverage*: Services (`AuthService`, `EmployeeService`, `RateLimitingService`) and Mappers.
2.  **E2E Tests**: Verify the full request lifecycle from Controller to Database.
    -   *Tools*: RestAssured, H2 Database.
    -   *Scope*: 
        -   **Auth Journey**: Registration flow, Login success, Brute-force protection verification.
        -   **Employee Journey**: Full CRUD life-cycle, verifying that "deleted" employees are actually soft-deleted in the DB but inaccessible via API.
        -   **Security Regression**: Verifying RBAC (Users cannot delete employees) and lack of information leakage in errors.

---

## Security

Security is a primary focus of this application:
-   **No Information Leakage**: Default Spring Boot error attributes (stack traces) are disabled. Custom global exception handling returns sanitized 4xx/5xx responses.
-   **Brute Force Protection**: Login endpoints are rate-limited (5 attempts/min) per IP/User to prevent credential stuffing.
-   **Least Privilege**: `USER` role has read-only access to employee data; only `ADMIN` can modify state.
-   **Safe Defaults**: Uses `BCrypt` for password hashing and `HS256` for JWT signing.

---

## Future Improvements

-   [ ] **Docker Support**: Containerize the application and database with `docker-compose`.
-   [ ] **CI/CD Pipeline**: Automate testing and deployment using GitHub Actions.
-   [ ] **Refresh Token Rotation**: Enhance security by rotating refresh tokens on use.
-   [ ] **Monitoring**: Add Spring Actuator and Prometheus/Grafana for real-time metrics.

---

**Developed with ❤️ by [rfrmdhn](https://github.com/rfrmdhn)**
