package com.example.localbusiness.controller;

import com.example.localbusiness.model.Order;
import com.example.localbusiness.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final OrderService orderService;

    @PostMapping("/stripe")
    public void handleStripeWebhook(@RequestBody String payload,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        String sigHeader = request.getHeader("Stripe-Signature");
        
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            
            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            log.error("Webhook error", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private void handleCheckoutSessionCompleted(Event event) {
        try {
            Session session = (Session) event.getDataObjectDeserializer().getObject().get();
            String orderId = session.getMetadata().get("order_id");
            
            if (orderId != null) {
                orderService.updatePaymentStatus(Long.parseLong(orderId), Order.PaymentStatus.PAID);
                log.info("Payment completed for order: {}", orderId);
            }
        } catch (Exception e) {
            log.error("Error handling checkout session completed", e);
        }
    }
    
    private void handlePaymentSucceeded(Event event) {
        log.info("Payment succeeded event received");
    }
    
    private void handlePaymentFailed(Event event) {
        log.info("Payment failed event received");
    }
} 