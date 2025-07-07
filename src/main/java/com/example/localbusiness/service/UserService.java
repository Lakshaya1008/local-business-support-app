package com.example.localbusiness.service;

import com.example.localbusiness.dto.UserProfileRequest;
import com.example.localbusiness.dto.UserProfileResponse;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserProfileResponse getUserProfile(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            UserProfileResponse response = new UserProfileResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setPhone(null); // Not available in current User model
            response.setAddress(null); // Not available in current User model
            response.setRole(user.getRoles().iterator().hasNext() ? 
                user.getRoles().iterator().next().name() : "USER");
            
            log.info("Retrieved user profile for user ID: {}", userId);
            return response;
        } catch (Exception e) {
            log.error("Error retrieving user profile for user ID: {}", userId, e);
            throw e;
        }
    }
    
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            // Phone and address not available in current User model
            
            userRepository.save(user);
            
            UserProfileResponse response = new UserProfileResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setPhone(null); // Not available in current User model
            response.setAddress(null); // Not available in current User model
            response.setRole(user.getRoles().iterator().hasNext() ? 
                user.getRoles().iterator().next().name() : "USER");
            
            log.info("Updated user profile for user ID: {}", userId);
            return response;
        } catch (Exception e) {
            log.error("Error updating user profile for user ID: {}", userId, e);
            throw e;
        }
    }
} 