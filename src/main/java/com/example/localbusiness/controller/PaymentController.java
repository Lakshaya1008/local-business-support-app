package com.example.localbusiness.controller;

import com.example.localbusiness.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = request.get("currency").toString();
        String receipt = UUID.randomUUID().toString();

        String orderId = paymentService.createPaymentOrder(amount, currency, receipt);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("amount", amount);
        response.put("currency", currency);
        response.put("receipt", receipt);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> request) {
        String paymentId = request.get("paymentId");
        String orderId = request.get("orderId");
        String signature = request.get("signature");

        boolean isValid = paymentService.verifyPayment(paymentId, orderId, signature);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("paymentId", paymentId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> webhookData) {
        boolean processed = paymentService.processWebhook(webhookData);
        
        if (processed) {
            return ResponseEntity.ok("Webhook processed successfully");
        } else {
            return ResponseEntity.badRequest().body("Webhook processing failed");
        }
    }
} 