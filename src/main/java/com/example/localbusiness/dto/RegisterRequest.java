package com.example.localbusiness.dto;

import com.example.localbusiness.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private Role role;
} 