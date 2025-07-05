package com.example.localbusiness.dto;

import lombok.Data;

@Data
public class VendorApplicationRequest {
    private String businessName;
    private String businessDescription;
    private String businessAddress;
    private String pincode;
    private String phoneNumber;
    private String gstNumber;
    private String panNumber;
} 