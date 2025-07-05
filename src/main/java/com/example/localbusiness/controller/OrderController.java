package com.example.localbusiness.controller;

import com.example.localbusiness.dto.OrderRequest;
import com.example.localbusiness.dto.OrderResponse;
import com.example.localbusiness.model.Order;
import com.example.localbusiness.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        OrderResponse response = orderService.createOrder(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<OrderResponse> orders = orderService.getCustomerOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/seller")
    public ResponseEntity<List<OrderResponse>> getSellerOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<OrderResponse> orders = orderService.getSellerOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(response);
    }
} 