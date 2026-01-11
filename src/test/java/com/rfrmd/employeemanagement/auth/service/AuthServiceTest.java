package com.rfrmd.employeemanagement.auth.service;

import com.rfrmd.employeemanagement.auth.dto.AuthenticationResponse;
import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.Role;
import com.rfrmd.employeemanagement.auth.entity.User;
import com.rfrmd.employeemanagement.auth.mapper.AuthMapper;
import com.rfrmd.employeemanagement.auth.repository.UserRepository;
import com.rfrmd.employeemanagement.auth.security.JwtService;
import com.rfrmd.employeemanagement.auth.security.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private AuthMapper authMapper;
    @Mock
    private RateLimitingService rateLimitingService;
    @Mock
    private Bucket bucket;

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
        when(repository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(authMapper.toUser(registerRequest)).thenReturn(user); // Mapper must replace manual creation
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.accessToken());

        verify(repository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(repository.existsByEmail(registerRequest.email())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() {
        // Rate Limiting Mock
        when(rateLimitingService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.accessToken());

        verify(rateLimitingService).resolveBucket(loginRequest.email());
        verify(bucket).tryConsume(1);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_ShouldThrowBadCredentials_WhenRateLimitExceeded() {
        when(rateLimitingService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false); // Bucket empty

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginRequest));

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void authenticate_ShouldThrowException_WhenAuthenticationFails() {
        when(rateLimitingService.resolveBucket(anyString())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginRequest));
    }
}
