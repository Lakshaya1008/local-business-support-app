package com.example.localbusiness.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "vendor_applications")
public class VendorApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String businessDescription;

    @Column(nullable = false)
    private String businessAddress;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String gstNumber;

    @Column(nullable = false)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false)
    private LocalDateTime applicationDate;

    private LocalDateTime approvalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private String rejectionReason;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }
} 