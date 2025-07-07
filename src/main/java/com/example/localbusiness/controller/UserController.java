package com.example.localbusiness.controller;

import com.example.localbusiness.dto.UserProfileRequest;
import com.example.localbusiness.dto.UserProfileResponse;
import com.example.localbusiness.model.User;
import com.example.localbusiness.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently logged-in user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update the profile of the currently logged-in user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @RequestBody UserProfileRequest request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserProfileResponse profile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get user profile by ID", description = "Get a user's profile by their ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long userId) {
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
} 