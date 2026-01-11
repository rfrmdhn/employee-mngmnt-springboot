package com.rfrmd.employeemanagement.employee.controller;

import com.rfrmd.employeemanagement.employee.controller.EmployeeController;
import com.rfrmd.employeemanagement.employee.dto.EmployeeDto;
import com.rfrmd.employeemanagement.employee.service.EmployeeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeDto employeeDto;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        employeeDto = EmployeeDto.builder()
                .id(employeeId)
                .name("John Doe")
                .email("john@example.com")
                .position("Developer")
                .salary(50000.0)
                .department("IT")
                .build();
    }

    @Test
    void getAllEmployees_ShouldReturn200WithPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto));

        // Controller calls service with keyword=null
        when(employeeService.getAllEmployees(eq(null), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<EmployeeDto>> response = employeeController.getAllEmployees(null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getEmployeeById_ShouldReturn200WithDto() {
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeDto);

        ResponseEntity<EmployeeDto> response = employeeController.getEmployeeById(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().name());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {
        when(employeeService.getEmployeeById(employeeId))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Not found"));

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> employeeController.getEmployeeById(employeeId));
    }

    @Test
    void createEmployee_ShouldReturn200WithCreatedDto() {
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        ResponseEntity<EmployeeDto> response = employeeController.createEmployee(employeeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().name());
        verify(employeeService).createEmployee(any(EmployeeDto.class));
    }

    @Test
    void updateEmployee_ShouldReturn200WithUpdatedDto() {
        when(employeeService.updateEmployee(eq(employeeId), any(EmployeeDto.class))).thenReturn(employeeDto);

        ResponseEntity<EmployeeDto> response = employeeController.updateEmployee(employeeId, employeeDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(employeeService).updateEmployee(eq(employeeId), any(EmployeeDto.class));
    }

    @Test
    void deleteEmployee_ShouldReturn204() {
        doNothing().when(employeeService).deleteEmployee(employeeId);

        ResponseEntity<Void> response = employeeController.deleteEmployee(employeeId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(employeeService).deleteEmployee(employeeId);
    }
}
