package com.example.localbusiness.controller;

import com.example.localbusiness.dto.CreatePaymentRequest;
import com.example.localbusiness.dto.CreatePaymentResponse;
import com.example.localbusiness.service.CashfreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final CashfreeService cashfreeService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<CreatePaymentResponse> createCheckoutSession(@RequestBody CreatePaymentRequest request,
                                                                     Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            // Set default URLs if not provided
            if (request.getSuccessUrl() == null) {
                request.setSuccessUrl("http://localhost:8085/orders/success");
            }
            if (request.getCancelUrl() == null) {
                request.setCancelUrl("http://localhost:8085/cart");
            }
            
            CreatePaymentResponse response = cashfreeService.createPaymentSession(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            String orderId = request.get("orderId");
            String paymentId = request.get("paymentId");
            String signature = request.get("signature");
            
            boolean verified = cashfreeService.verifyPayment(orderId, paymentId, signature);
            return ResponseEntity.ok(Map.of("verified", verified));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("verified", false));
        }
    }
} 