package com.example.localbusiness.service;

import com.example.localbusiness.dto.AuthRequest;
import com.example.localbusiness.dto.AuthResponse;
import com.example.localbusiness.dto.RegisterRequest;
import com.example.localbusiness.model.Role;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.UserRepository;
import com.example.localbusiness.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Test User");
        registerRequest.setRoles(Set.of(Role.USER));

        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .roles(Set.of(Role.USER))
                .enabled(true)
                .build();
    }

    @Test
    void register_ShouldCreateNewUser_WhenValidRequest() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> authService.register(registerRequest));

        // Then
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(registerRequest));
        assertEquals("Email already registered", exception.getMessage());
        
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_ShouldReturnAuthResponse_WhenValidCredentials() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any())).thenReturn("jwtToken");

        // When
        AuthResponse response = authService.authenticate(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("USER", response.getRole());
        assertTrue(response.isEnabled());
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtUtil).generateToken(anyString(), any());
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(authRequest));
        assertEquals("Invalid credentials", exception.getMessage());
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotEnabled() {
        // Given
        testUser.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(authRequest));
        assertEquals("Email not verified", exception.getMessage());
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_ShouldThrowException_WhenInvalidPassword() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(authRequest));
        assertEquals("Invalid credentials", exception.getMessage());
        
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void verifyEmail_ShouldEnableUser_WhenValidToken() {
        // Given
        String token = "validToken";
        testUser.setEnabled(false);
        testUser.setVerificationToken(token);
        
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> authService.verifyEmail(token));

        // Then
        verify(userRepository).findByVerificationToken(token);
        verify(userRepository).save(any(User.class));
        assertTrue(testUser.isEnabled());
        assertNull(testUser.getVerificationToken());
    }

    @Test
    void verifyEmail_ShouldThrowException_WhenInvalidToken() {
        // Given
        String token = "invalidToken";
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.verifyEmail(token));
        assertEquals("Invalid verification token", exception.getMessage());
        
        verify(userRepository).findByVerificationToken(token);
        verify(userRepository, never()).save(any(User.class));
    }
} 