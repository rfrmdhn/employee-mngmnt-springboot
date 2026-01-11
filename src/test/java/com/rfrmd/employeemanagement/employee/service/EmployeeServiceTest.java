package com.rfrmd.employeemanagement.employee.service;

import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import com.rfrmd.employeemanagement.employee.entity.Employee;
import com.rfrmd.employeemanagement.employee.mapper.EmployeeMapper;
import com.rfrmd.employeemanagement.employee.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        employee = Employee.builder()
                .id(employeeId)
                .name("John Doe")
                .email("john@example.com")
                .position("Developer")
                .salary(java.math.BigDecimal.valueOf(50000.0))
                .department("IT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();

        employeeDto = EmployeeDto.builder()
                .id(employeeId)
                .name("John Doe")
                .email("john@example.com")
                .position("Developer")
                .salary(java.math.BigDecimal.valueOf(50000.0))
                .department("IT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllEmployees_ShouldReturnPageOfEmployeeDtos() {
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(employeePage);
        when(mapper.toDto(employee)).thenReturn(employeeDto);

        Page<EmployeeDto> result = employeeService.getAllEmployees(null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).name());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper).toDto(employee);
    }

    @Test
    void getEmployeeById_ShouldReturnEmployeeDto_WhenFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(mapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(mapper).toDto(employee);
    }

    @Test
    void createEmployee_ShouldReturnEmployeeDto_WhenEmailIsUnique() {
        when(repository.existsByEmail(employeeDto.email())).thenReturn(false);
        when(mapper.toEntity(employeeDto)).thenReturn(employee);
        when(repository.save(any(Employee.class))).thenReturn(employee);
        when(mapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.createEmployee(employeeDto);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(repository).save(any(Employee.class));
        verify(mapper).toEntity(employeeDto);
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedDto_WhenFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(repository.save(any(Employee.class))).thenReturn(employee);
        when(mapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.updateEmployee(employeeId, employeeDto);

        assertNotNull(result);
        verify(mapper).updateEntityFromDto(employeeDto, employee);
        verify(repository).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_ShouldDelete_WhenFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(repository.save(any(Employee.class))).thenReturn(employee);

        employeeService.deleteEmployee(employeeId);

        assertTrue(employee.isDeleted());
        verify(repository).save(employee);
    }

    @Test
    void deleteEmployee_ShouldThrowException_WhenNotFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> employeeService.deleteEmployee(employeeId));
    }
}
