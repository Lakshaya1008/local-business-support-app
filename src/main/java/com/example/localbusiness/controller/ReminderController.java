package com.example.localbusiness.controller;

import com.example.localbusiness.model.Reminder;
import com.example.localbusiness.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Reminder management endpoints")
public class ReminderController {
    
    private final ReminderService reminderService;
    
    @PostMapping
    @Operation(summary = "Create reminder", description = "Create a new reminder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminder created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid reminder data")
    })
    public ResponseEntity<Reminder> createReminder(
            @Parameter(description = "Reminder title") @RequestParam String title,
            @Parameter(description = "Reminder description") @RequestParam String description,
            @Parameter(description = "Reminder time") @RequestParam LocalDateTime reminderTime,
            @Parameter(description = "Reminder type") @RequestParam(defaultValue = "GENERAL") Reminder.ReminderType type,
            Authentication authentication) {
        
        Long userId = Long.parseLong(authentication.getName());
        Reminder reminder = reminderService.createReminder(userId, title, description, reminderTime, type);
        return ResponseEntity.ok(reminder);
    }
    
    @GetMapping
    @Operation(summary = "Get user reminders", description = "Get all reminders for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminders retrieved successfully")
    })
    public ResponseEntity<List<Reminder>> getUserReminders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<Reminder> reminders = reminderService.getUserReminders(userId);
        return ResponseEntity.ok(reminders);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update reminder", description = "Update an existing reminder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminder updated successfully"),
        @ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<String> updateReminder(
            @Parameter(description = "Reminder ID") @PathVariable Long id,
            @Parameter(description = "Reminder title") @RequestParam String title,
            @Parameter(description = "Reminder description") @RequestParam String description,
            @Parameter(description = "Reminder time") @RequestParam LocalDateTime reminderTime) {
        
        reminderService.updateReminder(id, title, description, reminderTime);
        return ResponseEntity.ok("Reminder updated successfully");
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reminder", description = "Delete a reminder")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reminder deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    public ResponseEntity<String> deleteReminder(
            @Parameter(description = "Reminder ID") @PathVariable Long id) {
        
        reminderService.deleteReminder(id);
        return ResponseEntity.ok("Reminder deleted successfully");
    }
} 