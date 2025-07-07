package com.example.localbusiness.controller;

import com.example.localbusiness.dto.CartResponse;
import com.example.localbusiness.model.CartItem;
import com.example.localbusiness.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
            BigDecimal total = cartService.getCartTotal(userId);
            int itemCount = cartService.getCartItemCount(userId);
            
            CartResponse response = CartResponse.builder()
                    .items(cartItems)
                    .totalAmount(total)
                    .itemCount(itemCount)
                    .isValid(cartService.validateCart(userId))
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting cart", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            boolean success = cartService.addToCart(userId, productId, quantity);
            
            if (success) {
                return getCart(authentication);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error adding item to cart", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            boolean success = cartService.updateCartItemQuantity(userId, productId, quantity);
            
            if (success) {
                return getCart(authentication);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error updating cart item quantity", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartResponse> removeFromCart(
            @RequestParam Long productId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            cartService.removeFromCart(userId, productId);
            
            return getCart(authentication);
        } catch (Exception e) {
            log.error("Error removing item from cart", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            cartService.clearCart(userId);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            int count = cartService.getCartItemCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting cart item count", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateCart(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean isValid = cartService.validateCart(userId);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("Error validating cart", e);
            return ResponseEntity.badRequest().build();
        }
    }
} 