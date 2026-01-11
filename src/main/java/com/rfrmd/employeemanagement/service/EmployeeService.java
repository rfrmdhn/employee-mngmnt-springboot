package com.rfrmd.employeemanagement.service;

import com.rfrmd.employeemanagement.dto.EmployeeDto;
import com.rfrmd.employeemanagement.model.Employee;
import com.rfrmd.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::mapToDto);
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return mapToDto(employee);
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
                .build();
        return mapToDto(repository.save(employee));
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto dto) {
        Employee employee = repository.findById(id)
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
    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        repository.deleteById(id);
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
