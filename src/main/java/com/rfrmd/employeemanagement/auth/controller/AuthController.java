package com.rfrmd.employeemanagement.auth.controller;

import com.rfrmd.employeemanagement.auth.dto.AuthenticationResponse;
import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Register a new user", description = "Creates a new user account with the specified role.")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
