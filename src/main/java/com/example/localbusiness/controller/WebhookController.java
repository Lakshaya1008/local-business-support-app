
package com.example.localbusiness.controller;

import com.example.localbusiness.model.Order;
import com.example.localbusiness.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    @Value("${cashfree.webhook-secret}")
    private String webhookSecret;

    private final OrderService orderService;

    @PostMapping("/cashfree")
    public void handleCashfreeWebhook(@RequestBody String payload,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        try {
            // Parse the webhook payload
            // Note: In production, you should verify the webhook signature
            // For now, we'll process the payload directly
            
            // Extract order ID from the payload
            // This is a simplified implementation - you should add proper signature verification
            
            log.info("Received Cashfree webhook: {}", payload);
            
            // Process the webhook based on event type
            // You would typically parse the JSON payload and check the event type
            // For now, we'll assume it's a payment success event
            
            // Extract order ID from the webhook payload
            // This is a placeholder - implement based on Cashfree's webhook format
            String orderId = extractOrderIdFromPayload(payload);
            
            if (orderId != null) {
                orderService.updatePaymentStatus(Long.parseLong(orderId), Order.PaymentStatus.PAID);
                log.info("Payment completed for order: {}", orderId);
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("Webhook error", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private String extractOrderIdFromPayload(String payload) {
        // This is a placeholder implementation
        // In a real implementation, you would parse the JSON payload
        // and extract the order ID based on Cashfree's webhook format
        
        // For now, return null - implement based on actual webhook structure
        return null;
    }
} 