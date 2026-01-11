package com.rfrmd.employeemanagement.employee.service;

import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import com.rfrmd.employeemanagement.employee.entity.Employee;
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
                .build();
    }

    // --- getAllEmployees ---
    @Test
    void getAllEmployees_ShouldReturnPageOfEmployeeDtos() {
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        // Mock findAll(Specification, Pageable) instead of findAll(Pageable)
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDto> result = employeeService.getAllEmployees(null, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).name());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllEmployees_WithKeyword_ShouldUseSpecification() {
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(employeePage);

        Page<EmployeeDto> result = employeeService.getAllEmployees("developer", Pageable.unpaged());

        assertNotNull(result);
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    // --- getEmployeeById ---
    @Test
    void getEmployeeById_ShouldReturnEmployeeDto_WhenFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        assertEquals("john@example.com", result.email());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.getEmployeeById(employeeId));
    }

    // --- createEmployee ---
    @Test
    void createEmployee_ShouldReturnEmployeeDto_WhenEmailIsUnique() {
        when(repository.existsByEmail(employeeDto.email())).thenReturn(false);
        when(repository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.createEmployee(employeeDto);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(repository).save(any(Employee.class));
    }

    @Test
    void createEmployee_ShouldThrowException_WhenEmailExists() {
        when(repository.existsByEmail(employeeDto.email())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(employeeDto));
        verify(repository, never()).save(any(Employee.class));
    }

    // --- updateEmployee ---
    @Test
    void updateEmployee_ShouldReturnUpdatedDto_WhenFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(employee));
        // No need to mock existsByEmail since email hasn't changed
        when(repository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.updateEmployee(employeeId, employeeDto);

        assertNotNull(result);
        verify(repository).save(any(Employee.class));
    }

    @Test
    void updateEmployee_ShouldThrowException_WhenNotFound() {
        when(repository.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.updateEmployee(employeeId, employeeDto));
    }

    @Test
    void updateEmployee_ShouldThrowException_WhenEmailConflict() {
        Employee existingEmployee = Employee.builder()
                .id(employeeId)
                .email("original@example.com")
                .deleted(false)
                .build();
        EmployeeDto newDto = EmployeeDto.builder()
                .email("taken@example.com")
                .build();

        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(repository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(employeeId, newDto));
    }

    // --- deleteEmployee ---
    @Test
    void deleteEmployee_ShouldDelete_WhenFound() {
        // Soft delete implementation: findById -> setDeleted(true) -> save
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
        verify(repository, never()).save(any());
    }
}
