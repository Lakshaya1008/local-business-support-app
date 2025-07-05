package com.example.localbusiness.controller;

import com.example.localbusiness.dto.AuthRequest;
import com.example.localbusiness.dto.RegisterRequest;
import com.example.localbusiness.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterAndLogin() throws Exception {
        // Use unique email with timestamp to avoid conflicts
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        
        RegisterRequest register = new RegisterRequest();
        register.setEmail(uniqueEmail);
        register.setPassword("password");
        register.setName("Test User");
        register.setRoles(Set.of(Role.USER));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());
        
        // @Transactional will automatically rollback the test data
    }
} 