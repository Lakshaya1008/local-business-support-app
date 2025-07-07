package com.example.localbusiness.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
} 