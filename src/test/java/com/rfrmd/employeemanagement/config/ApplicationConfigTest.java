package com.rfrmd.employeemanagement.config;

import com.rfrmd.employeemanagement.model.User;
import com.rfrmd.employeemanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authConfig;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    void userDetailsService_ShouldReturnUserDetails_WhenUserExists() {
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        var userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void userDetailsService_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown@example.com"));
    }

    @Test
    void authenticationProvider_ShouldReturnProvider() {
        AuthenticationProvider provider = applicationConfig.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void authenticationManager_ShouldReturnManager() throws Exception {
        when(authConfig.getAuthenticationManager()).thenReturn(authManager);
        
        AuthenticationManager manager = applicationConfig.authenticationManager(authConfig);
        
        assertNotNull(manager);
        assertEquals(authManager, manager);
    }

    @Test
    void passwordEncoder_ShouldReturnEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        assertNotNull(encoder);
    }
}
