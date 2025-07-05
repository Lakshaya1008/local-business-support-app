package com.example.localbusiness.service;

import com.example.localbusiness.config.StripeConfig;
import com.example.localbusiness.dto.CreatePaymentRequest;
import com.example.localbusiness.dto.CreatePaymentResponse;
import com.example.localbusiness.dto.OrderRequest;
import com.example.localbusiness.dto.OrderResponse;
import com.example.localbusiness.model.Product;
import com.example.localbusiness.repository.ProductRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    
    private final StripeConfig stripeConfig;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    
    public CreatePaymentResponse createPaymentSession(CreatePaymentRequest request, Long userId) throws StripeException {
        // First create order in your system
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItems(request.getItems());
        orderRequest.setDeliveryAddress(request.getDeliveryAddress());
        orderRequest.setPaymentMethod("STRIPE");
        
        OrderResponse order = orderService.createOrder(orderRequest, userId);
        
        // Create Stripe session
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(request.getSuccessUrl() + "?order_id=" + order.getId())
            .setCancelUrl(request.getCancelUrl())
            .putMetadata("order_id", order.getId().toString());

        // Add line items from cart
        for (var item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity((long) item.getQuantity())
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(request.getCurrency())
                        .setUnitAmount(product.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(product.getName())
                                .build()
                        )
                        .build()
                )
                .build();
            paramsBuilder.addLineItem(lineItem);
        }

        Session session = Session.create(paramsBuilder.build());
        log.info("Created Stripe session: {} for order: {}", session.getId(), order.getId());

        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setSessionId(session.getId());
        response.setPublishableKey(stripeConfig.getPublishableKey());
        response.setOrderId(order.getId().toString());
        
        return response;
    }
} 