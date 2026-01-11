package com.rfrmd.employeemanagement.employee.service;

import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import com.rfrmd.employeemanagement.employee.entity.Employee;
import com.rfrmd.employeemanagement.employee.mapper.EmployeeMapper;
import com.rfrmd.employeemanagement.employee.repository.EmployeeRepository;
import com.rfrmd.employeemanagement.employee.repository.spec.EmployeeSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<EmployeeDto> getAllEmployees(String keyword, Pageable pageable) {
        return repository.findAll(EmployeeSpecifications.withKeyword(keyword), pageable)
                .map(mapper::toDto);
    }

    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        return getAllEmployees(null, pageable);
    }

    public EmployeeDto getEmployeeById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        Employee employee = mapper.toEntity(dto);
        employee.setDeleted(false);
        return mapper.toDto(repository.save(employee));
    }

    @Transactional
    public EmployeeDto updateEmployee(UUID id, EmployeeDto dto) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        if (!employee.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already taken");
        }

        mapper.updateEntityFromDto(dto, employee);
        return mapper.toDto(repository.save(employee));
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        employee.setDeleted(true);
        repository.save(employee);
    }
}
