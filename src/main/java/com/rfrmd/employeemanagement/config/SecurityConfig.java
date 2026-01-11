package com.rfrmd.employeemanagement.config;

import com.rfrmd.employeemanagement.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rfrmd.employeemanagement.security.SecurityConstants;

import static com.example.demo.model.Role.ADMIN;
import static com.example.demo.model.Role.USER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(
                                        SecurityConstants.AUTH_WHITELIST,
                                        SecurityConstants.API_DOCS,
                                        SecurityConstants.SWAGGER_UI,
                                        SecurityConstants.SWAGGER_UI_HTML
                                )
                                .permitAll()
                                .requestMatchers(GET, SecurityConstants.EMPLOYEES_ENDPOINT).hasAnyAuthority(ADMIN.name(), USER.name())
                                .requestMatchers(POST, SecurityConstants.EMPLOYEES_ENDPOINT).hasAuthority(ADMIN.name())
                                .requestMatchers(PUT, SecurityConstants.EMPLOYEES_ENDPOINT).hasAuthority(ADMIN.name())
                                .requestMatchers(DELETE, SecurityConstants.EMPLOYEES_ENDPOINT).hasAuthority(ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
