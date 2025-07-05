package com.example.localbusiness.controller;

import com.example.localbusiness.dto.VendorApplicationRequest;
import com.example.localbusiness.model.VendorApplication;
import com.example.localbusiness.service.VendorApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-applications")
@RequiredArgsConstructor
public class VendorApplicationController {
    private final VendorApplicationService vendorApplicationService;

    @PostMapping
    public ResponseEntity<VendorApplication> createApplication(
            @RequestBody VendorApplicationRequest request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        VendorApplication application = vendorApplicationService.createApplication(request, userId);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<VendorApplication>> getPendingApplications() {
        List<VendorApplication> applications = vendorApplicationService.getPendingApplications();
        return ResponseEntity.ok(applications);
    }

    @PostMapping("/{applicationId}/approve")
    public ResponseEntity<VendorApplication> approveApplication(
            @PathVariable Long applicationId,
            Authentication authentication) {
        Long adminId = Long.parseLong(authentication.getName());
        VendorApplication application = vendorApplicationService.approveApplication(applicationId, adminId);
        return ResponseEntity.ok(application);
    }

    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<VendorApplication> rejectApplication(
            @PathVariable Long applicationId,
            @RequestParam String reason,
            Authentication authentication) {
        Long adminId = Long.parseLong(authentication.getName());
        VendorApplication application = vendorApplicationService.rejectApplication(applicationId, reason, adminId);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/user")
    public ResponseEntity<VendorApplication> getUserApplication(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        VendorApplication application = vendorApplicationService.getApplicationByUserId(userId);
        return ResponseEntity.ok(application);
    }
} 