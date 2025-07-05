package com.example.localbusiness.controller;

import com.example.localbusiness.dto.ProductRequest;
import com.example.localbusiness.model.Role;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private String sellerEmail = "seller@example.com";

    @BeforeEach
    void setup() {
        if (userRepository.findByEmail(sellerEmail).isEmpty()) {
            User seller = User.builder()
                    .email(sellerEmail)
                    .password(passwordEncoder.encode("password"))
                    .name("Seller")
                    .roles(Set.of(Role.SELLER))
                    .enabled(true)
                    .build();
            userRepository.save(seller);
        }
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }
} 