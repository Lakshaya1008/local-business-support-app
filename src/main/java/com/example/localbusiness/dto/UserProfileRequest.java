package com.example.localbusiness.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
} 