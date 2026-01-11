package com.rfrmd.employeemanagement.service;

import com.rfrmd.employeemanagement.dto.EmployeeDto;
import com.rfrmd.employeemanagement.model.Employee;
import com.rfrmd.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .position("Developer")
                .salary(50000.0)
                .department("IT")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        employeeDto = EmployeeDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .position("Developer")
                .salary(50000.0)
                .department("IT")
                .build();
    }

    // --- getAllEmployees ---
    @Test
    void getAllEmployees_ShouldReturnPageOfEmployeeDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(repository.findAll(pageable)).thenReturn(employeePage);

        Page<EmployeeDto> result = employeeService.getAllEmployees(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
    }

    // --- getEmployeeById ---
    @Test
    void getEmployeeById_ShouldReturnEmployeeDto_WhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    // --- createEmployee ---
    @Test
    void createEmployee_ShouldReturnEmployeeDto_WhenEmailIsUnique() {
        when(repository.existsByEmail(employeeDto.getEmail())).thenReturn(false);
        when(repository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.createEmployee(employeeDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(repository).save(any(Employee.class));
    }

    @Test
    void createEmployee_ShouldThrowException_WhenEmailExists() {
        when(repository.existsByEmail(employeeDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(employeeDto));
        verify(repository, never()).save(any(Employee.class));
    }

    // --- updateEmployee ---
    @Test
    void updateEmployee_ShouldReturnUpdatedDto_WhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        // No need to mock existsByEmail since email hasn't changed
        when(repository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.updateEmployee(1L, employeeDto);

        assertNotNull(result);
        verify(repository).save(any(Employee.class));
    }

    @Test
    void updateEmployee_ShouldThrowException_WhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.updateEmployee(1L, employeeDto));
    }

    @Test
    void updateEmployee_ShouldThrowException_WhenEmailConflict() {
        Employee existingEmployee = Employee.builder()
                .id(1L)
                .email("original@example.com")
                .build();
        EmployeeDto newDto = EmployeeDto.builder()
                .email("taken@example.com")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingEmployee));
        when(repository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1L, newDto));
    }

    // --- deleteEmployee ---
    @Test
    void deleteEmployee_ShouldDelete_WhenFound() {
        when(repository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteEmployee_ShouldThrowException_WhenNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> employeeService.deleteEmployee(1L));
        verify(repository, never()).deleteById(any());
    }
}
