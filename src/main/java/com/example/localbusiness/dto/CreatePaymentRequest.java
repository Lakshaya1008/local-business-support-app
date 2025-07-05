package com.example.localbusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private BigDecimal amount;
    private String currency;
    private String successUrl;
    private String cancelUrl;
    private List<OrderItemRequest> items;
    private String deliveryAddress;
} 