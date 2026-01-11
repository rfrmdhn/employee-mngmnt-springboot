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
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        Employee employee = Employee.builder()
                .name(dto.name())
                .email(dto.email())
                .position(dto.position())
                .salary(dto.salary())
                .department(dto.department())
                .isDeleted(false)
                .build();
        return mapToDto(repository.save(employee));
    }

    @Transactional
    public EmployeeDto updateEmployee(UUID id, EmployeeDto dto) {
        Employee employee = repository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already taken");
        }

        employee.setName(dto.name());
        employee.setEmail(dto.email());
        employee.setPosition(dto.position());
        employee.setSalary(dto.salary());
        employee.setDepartment(dto.department());

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
