package com.example.localbusiness.service;

import com.example.localbusiness.dto.VendorApplicationRequest;
import com.example.localbusiness.model.Role;
import com.example.localbusiness.model.User;
import com.example.localbusiness.model.VendorApplication;
import com.example.localbusiness.repository.UserRepository;
import com.example.localbusiness.repository.VendorApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorApplicationService {
    private final VendorApplicationRepository vendorApplicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public VendorApplication createApplication(VendorApplicationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        VendorApplication application = VendorApplication.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .businessDescription(request.getBusinessDescription())
                .businessAddress(request.getBusinessAddress())
                .pincode(request.getPincode())
                .phoneNumber(request.getPhoneNumber())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .status(VendorApplication.ApplicationStatus.PENDING)
                .applicationDate(LocalDateTime.now())
                .build();

        return vendorApplicationRepository.save(application);
    }

    public List<VendorApplication> getPendingApplications() {
        return vendorApplicationRepository.findByStatus(VendorApplication.ApplicationStatus.PENDING);
    }

    @Transactional
    public VendorApplication approveApplication(Long applicationId, Long adminId) {
        VendorApplication application = vendorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        application.setStatus(VendorApplication.ApplicationStatus.APPROVED);
        application.setApprovalDate(LocalDateTime.now());
        application.setApprovedBy(admin);

        User user = application.getUser();
        user.getRoles().add(Role.SELLER);
        userRepository.save(user);

        return vendorApplicationRepository.save(application);
    }

    @Transactional
    public VendorApplication rejectApplication(Long applicationId, String reason, Long adminId) {
        VendorApplication application = vendorApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        application.setStatus(VendorApplication.ApplicationStatus.REJECTED);
        application.setApprovalDate(LocalDateTime.now());
        application.setApprovedBy(admin);
        application.setRejectionReason(reason);

        return vendorApplicationRepository.save(application);
    }

    public VendorApplication getApplicationByUserId(Long userId) {
        return vendorApplicationRepository.findByUserId(userId)
                .orElse(null);
    }
} 