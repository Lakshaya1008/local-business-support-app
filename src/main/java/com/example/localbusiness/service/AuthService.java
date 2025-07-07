package com.example.localbusiness.service;

import com.example.localbusiness.dto.*;
import com.example.localbusiness.model.*;
import com.example.localbusiness.repository.UserRepository;
import com.example.localbusiness.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email already registered");
        
        String token = UUID.randomUUID().toString();
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(Collections.singleton(request.getRole()))
                .enabled(true)
                .verificationToken(token)
                .build();
        userRepository.save(user);
        
        try {
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.warn("Failed to send verification email, but user registration succeeded: {}", e.getMessage());
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!user.isEnabled())
            throw new RuntimeException("Email not verified");
        
        System.out.println("DB password: [" + user.getPassword() + "]");
        System.out.println("Form password: [" + request.getPassword() + "]");
        
        if (!request.getPassword().equals(user.getPassword()))
            throw new RuntimeException("Invalid credentials");
        
        String token = jwtUtil.generateToken(user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            null, // Phone field not available in User model
            user.getRoles().iterator().next().name(),
            user.isEnabled()
        );
        
        return new AuthResponse(token, userResponse);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            null, // Phone field not available in User model
            user.getRoles().iterator().next().name(),
            user.isEnabled()
        );
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
} 