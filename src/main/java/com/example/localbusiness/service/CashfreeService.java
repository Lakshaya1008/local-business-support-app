package com.example.localbusiness.service;

import com.example.localbusiness.config.CashfreeConfig;
import com.example.localbusiness.dto.CreatePaymentRequest;
import com.example.localbusiness.dto.CreatePaymentResponse;
import com.example.localbusiness.dto.OrderRequest;
import com.example.localbusiness.dto.OrderResponse;
import com.example.localbusiness.model.Product;
import com.example.localbusiness.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashfreeService {
    
    private final CashfreeConfig cashfreeConfig;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public CreatePaymentResponse createPaymentSession(CreatePaymentRequest request, Long userId) {
        try {
            // First create order in your system
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setItems(request.getItems());
            orderRequest.setDeliveryAddress(request.getDeliveryAddress());
            orderRequest.setPaymentMethod("CASHFREE");
            
            OrderResponse order = orderService.createOrder(orderRequest, userId);
            
            // Create Cashfree payment session
            String apiUrl = cashfreeConfig.isProduction() 
                ? "https://api.cashfree.com/pg/orders"
                : "https://sandbox.cashfree.com/pg/orders";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", cashfreeConfig.getAppId());
            headers.set("x-client-secret", cashfreeConfig.getSecretKey());
            headers.set("x-api-version", "2023-08-01");
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("order_id", "ORDER_" + order.getId());
            requestBody.put("order_amount", request.getAmount());
            requestBody.put("order_currency", request.getCurrency().toUpperCase());
            requestBody.put("customer_details", createCustomerDetails(userId));
            requestBody.put("order_meta", createOrderMeta(order.getId()));
            requestBody.put("order_note", "Payment for order #" + order.getId());
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                CreatePaymentResponse paymentResponse = new CreatePaymentResponse();
                paymentResponse.setOrderId(order.getId().toString());
                paymentResponse.setSessionId((String) responseBody.get("payment_session_id"));
                paymentResponse.setPublishableKey(cashfreeConfig.getAppId());
                
                log.info("Created Cashfree payment session: {} for order: {}", 
                    paymentResponse.getSessionId(), order.getId());
                
                return paymentResponse;
            } else {
                throw new RuntimeException("Failed to create Cashfree payment session");
            }
            
        } catch (Exception e) {
            log.error("Error creating Cashfree payment session", e);
            throw new RuntimeException("Payment session creation failed", e);
        }
    }
    
    private Map<String, Object> createCustomerDetails(Long userId) {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", "CUST_" + userId);
        customerDetails.put("customer_name", "Customer " + userId);
        customerDetails.put("customer_email", "customer" + userId + "@example.com");
        customerDetails.put("customer_phone", "9999999999");
        return customerDetails;
    }
    
    private Map<String, Object> createOrderMeta(Long orderId) {
        Map<String, Object> orderMeta = new HashMap<>();
        orderMeta.put("return_url", "http://localhost:8085/orders/success?order_id=" + orderId);
        orderMeta.put("notify_url", "http://localhost:8085/webhook/cashfree");
        return orderMeta;
    }
    
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            String apiUrl = cashfreeConfig.isProduction() 
                ? "https://api.cashfree.com/pg/orders/" + orderId + "/payments"
                : "https://sandbox.cashfree.com/pg/orders/" + orderId + "/payments";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", cashfreeConfig.getAppId());
            headers.set("x-client-secret", cashfreeConfig.getSecretKey());
            headers.set("x-api-version", "2023-08-01");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String paymentStatus = (String) responseBody.get("payment_status");
                return "SUCCESS".equals(paymentStatus);
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error verifying Cashfree payment", e);
            return false;
        }
    }
} 