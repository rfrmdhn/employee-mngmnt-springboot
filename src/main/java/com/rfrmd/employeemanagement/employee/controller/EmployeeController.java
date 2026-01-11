package com.rfrmd.employeemanagement.employee.controller;

import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import com.rfrmd.employeemanagement.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Employee Management", description = "Endpoints for managing employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get all employees", description = "Retrieves a paginated list of employees. Supports keyword search.")
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(service.getAllEmployees(keyword, pageable));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get employee by ID", description = "Retrieves a specific employee by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Create employee", description = "Creates a new employee record. Requires ADMIN role.")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto dto) {
        return ResponseEntity.ok(service.createEmployee(dto));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update employee", description = "Updates an existing employee record. Requires ADMIN role.")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeDto dto) {
        return ResponseEntity.ok(service.updateEmployee(id, dto));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete employee", description = "Deletes an employee record (Soft Delete). Requires ADMIN role.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
