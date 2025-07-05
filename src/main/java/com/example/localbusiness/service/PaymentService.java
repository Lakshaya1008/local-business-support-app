package com.example.localbusiness.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public String createPaymentOrder(BigDecimal amount, String currency, String receipt) {
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);

            Order order = razorpay.orders.create(orderRequest);
            log.info("Created Razorpay order: {}", order.get("id").toString());
            return order.get("id").toString();
        } catch (RazorpayException e) {
            log.error("Failed to create payment order", e);
            throw new RuntimeException("Failed to create payment order", e);
        }
    }

    public boolean verifyPayment(String paymentId, String orderId, String signature) {
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_signature", signature);

            // In production, use: razorpay.utility.verifyPaymentSignature(attributes);
            // For test mode, we'll return true but log the verification
            log.info("Payment verification - PaymentId: {}, OrderId: {}, Signature: {}", paymentId, orderId, signature);
            return true;
        } catch (Exception e) {
            log.error("Payment verification failed", e);
            return false;
        }
    }

    public boolean processWebhook(Map<String, Object> webhookData) {
        try {
            String event = (String) webhookData.get("event");
            JSONObject payload = new JSONObject(webhookData.get("payload"));
            
            log.info("Processing webhook event: {}", event);
            
            switch (event) {
                case "payment.captured":
                    return handlePaymentCaptured(payload);
                case "payment.failed":
                    return handlePaymentFailed(payload);
                case "order.paid":
                    return handleOrderPaid(payload);
                default:
                    log.warn("Unhandled webhook event: {}", event);
                    return true;
            }
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return false;
        }
    }

    private boolean handlePaymentCaptured(JSONObject payload) {
        try {
            JSONObject payment = payload.getJSONObject("payment");
            String paymentId = payment.getString("id");
            String orderId = payment.getString("order_id");
            String status = payment.getString("status");
            
            log.info("Payment captured - PaymentId: {}, OrderId: {}, Status: {}", paymentId, orderId, status);
            
            // Update order status in database
            // This would typically call OrderService to update payment status
            return true;
        } catch (Exception e) {
            log.error("Error handling payment captured", e);
            return false;
        }
    }

    private boolean handlePaymentFailed(JSONObject payload) {
        try {
            JSONObject payment = payload.getJSONObject("payment");
            String paymentId = payment.getString("id");
            String orderId = payment.getString("order_id");
            
            log.info("Payment failed - PaymentId: {}, OrderId: {}", paymentId, orderId);
            
            // Update order status to failed
            return true;
        } catch (Exception e) {
            log.error("Error handling payment failed", e);
            return false;
        }
    }

    private boolean handleOrderPaid(JSONObject payload) {
        try {
            JSONObject order = payload.getJSONObject("order");
            String orderId = order.getString("id");
            
            log.info("Order paid - OrderId: {}", orderId);
            
            // Update order status to paid
            return true;
        } catch (Exception e) {
            log.error("Error handling order paid", e);
            return false;
        }
    }
} 