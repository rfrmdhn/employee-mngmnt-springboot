package com.rfrmd.employeemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public class EmployeeDto {

    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Position is required")
    private String position;

    @Min(value = 0, message = "Salary must be positive")
    private Double salary;

    private String department;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeDto() {
    }

    public EmployeeDto(UUID id, String name, String email, String position, Double salary, String department,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.position = position;
        this.salary = salary;
        this.department = department;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmployeeDtoBuilder builder() {
        return new EmployeeDtoBuilder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class EmployeeDtoBuilder {
        private UUID id;
        private String name;
        private String email;
        private String position;
        private Double salary;
        private String department;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public EmployeeDtoBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public EmployeeDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EmployeeDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public EmployeeDtoBuilder position(String position) {
            this.position = position;
            return this;
        }

        public EmployeeDtoBuilder salary(Double salary) {
            this.salary = salary;
            return this;
        }

        public EmployeeDtoBuilder department(String department) {
            this.department = department;
            return this;
        }

        public EmployeeDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EmployeeDtoBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public EmployeeDto build() {
            return new EmployeeDto(id, name, email, position, salary, department, createdAt, updatedAt);
        }
    }
}
