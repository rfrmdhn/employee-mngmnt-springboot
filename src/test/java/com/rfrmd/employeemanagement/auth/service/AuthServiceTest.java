package com.rfrmd.employeemanagement.auth.service;

import com.rfrmd.employeemanagement.auth.dto.AuthenticationResponse;
import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.Role;
import com.rfrmd.employeemanagement.auth.entity.User;
import com.rfrmd.employeemanagement.auth.repository.UserRepository;
import com.rfrmd.employeemanagement.auth.security.JwtService;
import com.rfrmd.employeemanagement.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("John Doe", "john@example.com", "password", Role.USER);

        loginRequest = new LoginRequest("john@example.com", "password");

        user = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenRequestIsValid() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.accessToken());

        verify(passwordEncoder).encode(registerRequest.password());
        verify(repository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() {
        when(repository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.accessToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByEmail(loginRequest.email());
        verify(jwtService).generateToken(user);
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        when(repository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> authService.authenticate(loginRequest));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByEmail(loginRequest.email());
        verifyNoInteractions(jwtService);
    }
}
