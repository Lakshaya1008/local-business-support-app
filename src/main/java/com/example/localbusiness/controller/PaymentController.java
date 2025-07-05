package com.example.localbusiness.controller;

import com.example.localbusiness.dto.CreatePaymentRequest;
import com.example.localbusiness.dto.CreatePaymentResponse;
import com.example.localbusiness.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final StripeService stripeService;

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
            
            CreatePaymentResponse response = stripeService.createPaymentSession(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 