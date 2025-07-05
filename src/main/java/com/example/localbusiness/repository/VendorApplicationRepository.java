package com.example.localbusiness.repository;

import com.example.localbusiness.model.VendorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorApplicationRepository extends JpaRepository<VendorApplication, Long> {
    List<VendorApplication> findByStatus(VendorApplication.ApplicationStatus status);
    Optional<VendorApplication> findByUserId(Long userId);
} 