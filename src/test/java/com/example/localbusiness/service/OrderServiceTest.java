package com.example.localbusiness.service;

import com.example.localbusiness.dto.OrderRequest;
import com.example.localbusiness.dto.OrderItemRequest;
import com.example.localbusiness.dto.OrderResponse;
import com.example.localbusiness.model.*;
import com.example.localbusiness.repository.OrderRepository;
import com.example.localbusiness.repository.ProductRepository;
import com.example.localbusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private OrderService orderService;

    private User customer;
    private User seller;
    private Product product;
    private Order order;
    private OrderRequest orderRequest;
    private OrderItemRequest orderItemRequest;

    @BeforeEach
    void setUp() {
        customer = User.builder().id(1L).email("customer@example.com").name("Customer").roles(Set.of(Role.USER)).enabled(true).build();
        seller = User.builder().id(2L).email("seller@example.com").name("Seller").roles(Set.of(Role.SELLER)).enabled(true).build();
        product = Product.builder().id(10L).name("Product").price(BigDecimal.valueOf(100)).quantity(10).seller(seller).build();
        orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(10L);
        orderItemRequest.setQuantity(2);
        orderRequest = new OrderRequest();
        orderRequest.setItems(List.of(orderItemRequest));
        orderRequest.setDeliveryAddress("123 Main St");
        orderRequest.setPaymentMethod("CASHFREE");
        order = Order.builder()
                .id(100L)
                .customer(customer)
                .seller(seller)
                .orderItems(new ArrayList<>())
                .totalAmount(BigDecimal.valueOf(200))
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryAddress("123 Main St")
                .paymentMethod("CASHFREE")
                .paymentStatusEnum(Order.PaymentStatus.PENDING)
                .build();
    }

    @Test
    void createOrder_ShouldCreateOrder_WhenValidRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        // Simulate product stock update
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setQuantity(p.getQuantity() - 2);
            return p;
        }).when(productRepository).save(any(Product.class));

        OrderResponse response = orderService.createOrder(orderRequest, 1L);
        assertNotNull(response);
        assertEquals("Customer", response.getCustomerName());
        assertEquals("Seller", response.getSellerName());
        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        verify(userRepository).findById(1L);
        verify(productRepository).findById(10L);
        verify(productRepository).save(any(Product.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenInsufficientStock() {
        product.setQuantity(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequest, 1L));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getCustomerOrders_ShouldReturnOrderResponses() {
        when(orderRepository.findByCustomerIdOrderByOrderDateDesc(1L)).thenReturn(List.of(order));
        List<OrderResponse> responses = orderService.getCustomerOrders(1L);
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Customer", responses.get(0).getCustomerName());
        verify(orderRepository).findByCustomerIdOrderByOrderDateDesc(1L);
    }

    @Test
    void getSellerOrders_ShouldReturnOrderResponses() {
        when(orderRepository.findBySellerIdOrderByOrderDateDesc(2L)).thenReturn(List.of(order));
        List<OrderResponse> responses = orderService.getSellerOrders(2L);
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Seller", responses.get(0).getSellerName());
        verify(orderRepository).findBySellerIdOrderByOrderDateDesc(2L);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        OrderResponse response = orderService.updateOrderStatus(100L, Order.OrderStatus.CONFIRMED);
        assertNotNull(response);
        assertEquals("CONFIRMED", response.getStatus());
        verify(orderRepository).findById(100L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(999L, Order.OrderStatus.CONFIRMED));
        assertTrue(ex.getMessage().contains("Order not found"));
    }
} 