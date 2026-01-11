package com.rfrmd.employeemanagement.service;

import com.rfrmd.employeemanagement.dto.EmployeeDto;
import com.rfrmd.employeemanagement.model.Employee;
import com.rfrmd.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Page<EmployeeDto> getAllEmployees(String keyword, Pageable pageable) {
        Specification<Employee> spec = (root, query, criteriaBuilder) -> {
            Predicate isNotDeleted = criteriaBuilder.equal(root.get("isDeleted"), false);

            if (keyword == null || keyword.isEmpty()) {
                return isNotDeleted;
            }

            String likePattern = "%" + keyword.toLowerCase() + "%";
            Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
            Predicate departmentLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("department")), likePattern);
            Predicate positionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), likePattern);

            return criteriaBuilder.and(isNotDeleted, criteriaBuilder.or(nameLike, departmentLike, positionLike));
        };

        return repository.findAll(spec, pageable)
                .map(this::mapToDto);
    }

    // Fallback for controller tests that might use the old signature
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        return getAllEmployees(null, pageable);
    }

    public EmployeeDto getEmployeeById(UUID id) {
        return repository.findById(id)
                .filter(e -> !e.isDeleted())
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }
        Employee employee = Employee.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .position(dto.getPosition())
                .salary(dto.getSalary())
                .department(dto.getDepartment())
                .isDeleted(false)
                .build();
        return mapToDto(repository.save(employee));
    }

    @Transactional
    public EmployeeDto updateEmployee(UUID id, EmployeeDto dto) {
        Employee employee = repository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());
        employee.setDepartment(dto.getDepartment());

        return mapToDto(repository.save(employee));
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = repository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        employee.setDeleted(true);
        repository.save(employee);
    }

    private EmployeeDto mapToDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .department(employee.getDepartment())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
