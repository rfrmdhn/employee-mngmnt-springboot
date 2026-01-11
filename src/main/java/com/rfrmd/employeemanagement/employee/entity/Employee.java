package com.rfrmd.employeemanagement.employee.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("is_deleted = false")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String position;
    private java.math.BigDecimal salary;
    private String department;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @org.springframework.data.annotation.CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @org.springframework.data.annotation.LastModifiedBy
    @Column(insertable = false)
    private String updatedBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    public Employee() {
    }

    public Employee(java.util.UUID id, String name, String email, String position, java.math.BigDecimal salary,
            String department,
            boolean deleted, String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.position = position;
        this.salary = salary;
        this.department = department;
        this.deleted = deleted;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public java.util.UUID getId() {
        return id;
    }

    public void setId(java.util.UUID id) {
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

    public java.math.BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(java.math.BigDecimal salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public static class EmployeeBuilder {
        private java.util.UUID id;
        private String name;
        private String email;
        private String position;
        private java.math.BigDecimal salary;
        private String department;
        private boolean deleted;
        private String createdBy;
        private String updatedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        EmployeeBuilder() {
        }

        public EmployeeBuilder id(java.util.UUID id) {
            this.id = id;
            return this;
        }

        public EmployeeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EmployeeBuilder email(String email) {
            this.email = email;
            return this;
        }

        public EmployeeBuilder position(String position) {
            this.position = position;
            return this;
        }

        public EmployeeBuilder salary(java.math.BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        public EmployeeBuilder department(String department) {
            this.department = department;
            return this;
        }

        public EmployeeBuilder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public EmployeeBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public EmployeeBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public EmployeeBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EmployeeBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Employee build() {
            return new Employee(id, name, email, position, salary, department, deleted, createdBy, updatedBy,
                    createdAt, updatedAt);
        }
    }
}
