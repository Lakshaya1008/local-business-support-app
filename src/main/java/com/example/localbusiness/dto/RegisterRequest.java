package com.example.localbusiness.dto;

import com.example.localbusiness.model.Role;
import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private Set<Role> roles;
} 