package com.example.localbusiness.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Email Verification");
            message.setText("Please verify your email by clicking this link: http://localhost:8085/api/auth/verify?token=" + token);
            mailSender.send(message);
            log.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            log.warn("Failed to send verification email to: {}. Error: {}", email, e.getMessage());
            // In a real application, you might want to queue this for retry
            // For now, we'll just log the error and continue
        }
    }

    public void sendPasswordResetEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset");
            message.setText("Reset your password by clicking this link: http://localhost:8085/api/auth/reset-password?token=" + token);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.warn("Failed to send password reset email to: {}. Error: {}", email, e.getMessage());
        }
    }
} 