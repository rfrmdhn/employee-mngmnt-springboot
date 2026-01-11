package com.rfrmd.employeemanagement.auth.service;

import com.rfrmd.employeemanagement.auth.dto.AuthenticationResponse;
import com.rfrmd.employeemanagement.auth.dto.LoginRequest;
import com.rfrmd.employeemanagement.auth.dto.RegisterRequest;
import com.rfrmd.employeemanagement.auth.entity.User;
import com.rfrmd.employeemanagement.auth.mapper.AuthMapper;
import com.rfrmd.employeemanagement.auth.repository.UserRepository;
import com.rfrmd.employeemanagement.auth.security.JwtService;
import com.rfrmd.employeemanagement.auth.security.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
        private final AuthMapper mapper;
        private final RateLimitingService rateLimitingService;

        public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService,
                        AuthenticationManager authenticationManager, AuthMapper mapper,
                        RateLimitingService rateLimitingService) {
                this.repository = repository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.authenticationManager = authenticationManager;
                this.mapper = mapper;
                this.rateLimitingService = rateLimitingService;
        }

        public AuthenticationResponse register(RegisterRequest request) {
                if (repository.existsByEmail(request.email())) {
                        throw new IllegalArgumentException("Email already exists");
                }
                User user = mapper.toUser(request);
                user.setPassword(passwordEncoder.encode(request.password()));
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return new AuthenticationResponse(jwtToken);
        }

        public AuthenticationResponse authenticate(LoginRequest request) {
                Bucket bucket = rateLimitingService.resolveBucket(request.email());
                if (!bucket.tryConsume(1)) {
                        throw new BadCredentialsException("Too many login attempts. Please try again later.");
                }

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.email(),
                                                request.password()));
                var user = (User) authentication.getPrincipal();
                var jwtToken = jwtService.generateToken(user);
                return new AuthenticationResponse(jwtToken);
        }
}
