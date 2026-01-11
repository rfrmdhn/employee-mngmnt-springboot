package com.rfrmd.employeemanagement.auth.service;

import com.rfrmd.employeemanagement.auth.dto.AuthenticationResponse;
import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;

import com.rfrmd.employeemanagement.auth.entity.User;
import com.rfrmd.employeemanagement.auth.repository.UserRepository;
import com.rfrmd.employeemanagement.auth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
                        AuthenticationManager authenticationManager) {
                this.repository = repository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.authenticationManager = authenticationManager;
        }

        public AuthenticationResponse register(RegisterRequest request) {
                var user = User.builder()
                                .name(request.name())
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .role(request.role())
                                .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return new AuthenticationResponse(jwtToken);
        }

        public AuthenticationResponse authenticate(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.email(),
                                                request.password()));
                var user = (User) authentication.getPrincipal();
                var jwtToken = jwtService.generateToken(user);
                return new AuthenticationResponse(jwtToken);
        }
}
