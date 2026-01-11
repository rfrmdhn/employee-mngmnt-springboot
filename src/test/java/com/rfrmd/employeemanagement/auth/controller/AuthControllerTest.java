package com.rfrmd.employeemanagement.auth.controller;

import com.rfrmd.employeemanagement.auth.controller.AuthController;
import com.rfrmd.employeemanagement.auth.dto.AuthenticationResponse;
import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.Role;
import com.rfrmd.employeemanagement.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John Doe", "john@example.com", "password", Role.USER);
        loginRequest = new LoginRequest("john@example.com", "password");
        authResponse = new AuthenticationResponse("jwt-token");
    }

    @Test
    void register_ShouldReturn200WithToken() {
        when(authService.register(registerRequest)).thenReturn(authResponse);

        ResponseEntity<AuthenticationResponse> response = authController.register(registerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().accessToken());
        verify(authService).register(registerRequest);
    }

    @Test
    void authenticate_ShouldReturn200WithToken() {
        when(authService.authenticate(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthenticationResponse> response = authController.authenticate(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().accessToken());
        verify(authService).authenticate(loginRequest);
    }
}
