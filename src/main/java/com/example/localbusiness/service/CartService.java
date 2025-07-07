package com.example.localbusiness.service;

import com.example.localbusiness.model.CartItem;
import com.example.localbusiness.model.Product;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.CartRepository;
import com.example.localbusiness.repository.ProductRepository;
import com.example.localbusiness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean addToCart(Long userId, Long productId, Integer quantity) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (productOpt.isEmpty() || userOpt.isEmpty()) {
                log.warn("Product or user not found - ProductId: {}, UserId: {}", productId, userId);
                return false;
            }
            
            Product product = productOpt.get();
            User user = userOpt.get();
            
            // Enhanced quantity validation
            if (quantity <= 0 || quantity > 100) {
                log.warn("Invalid quantity: {} (must be between 1 and 100)", quantity);
                return false;
            }
            
            // Check stock availability with buffer
            if (product.getQuantity() < quantity) {
                log.warn("Insufficient stock for product: {} (requested: {}, available: {})", 
                        product.getName(), quantity, product.getQuantity());
                return false;
            }
            
            // Check if product is active
            if (!"ACTIVE".equals(product.getStatus())) {
                log.warn("Product is not active: {}", product.getName());
                return false;
            }
            
            // Check if item already exists in cart
            Optional<CartItem> existingItem = cartRepository.findByBuyerIdAndProductId(userId, productId);
            
            if (existingItem.isPresent()) {
                // Update quantity
                CartItem cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + quantity;
                
                // Validate total quantity against stock
                if (product.getQuantity() < newQuantity) {
                    log.warn("Insufficient stock for updated quantity: {} (available: {})", newQuantity, product.getQuantity());
                    return false;
                }
                
                cartItem.setQuantity(newQuantity);
                cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
                cartItem.setUpdatedAt(LocalDateTime.now());
                cartRepository.save(cartItem);
                log.info("Updated cart item quantity for user: {}, product: {}, new quantity: {}", 
                        userId, productId, newQuantity);
            } else {
                // Create new cart item
                CartItem cartItem = new CartItem(user, product, quantity);
                cartItem.setCreatedAt(LocalDateTime.now());
                cartItem.setUpdatedAt(LocalDateTime.now());
                cartRepository.save(cartItem);
                log.info("Added new item to cart for user: {}, product: {}, quantity: {}", 
                        userId, productId, quantity);
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error adding item to cart", e);
            return false;
        }
    }

    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartRepository.findByBuyerIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public boolean updateCartItemQuantity(Long userId, Long productId, Integer newQuantity) {
        try {
            if (newQuantity <= 0) {
                // Remove item if quantity is 0 or negative
                removeFromCart(userId, productId);
                return true;
            }
            
            // Enhanced quantity validation
            if (newQuantity > 100) {
                log.warn("Quantity exceeds maximum limit: {}", newQuantity);
                return false;
            }
            
            Optional<CartItem> cartItemOpt = cartRepository.findByBuyerIdAndProductId(userId, productId);
            if (cartItemOpt.isEmpty()) {
                log.warn("Cart item not found for user: {}, product: {}", userId, productId);
                return false;
            }
            
            CartItem cartItem = cartItemOpt.get();
            Product product = cartItem.getProduct();
            
            // Validate stock availability
            if (product.getQuantity() < newQuantity) {
                log.warn("Insufficient stock for quantity update: {} (available: {})", newQuantity, product.getQuantity());
                return false;
            }
            
            // Check if product is still active
            if (!"ACTIVE".equals(product.getStatus())) {
                log.warn("Product is no longer active: {}", product.getName());
                return false;
            }
            
            cartItem.setQuantity(newQuantity);
            cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cartItem.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cartItem);
            
            log.info("Updated cart item quantity for user: {}, product: {}, new quantity: {}", 
                    userId, productId, newQuantity);
            return true;
        } catch (Exception e) {
            log.error("Error updating cart item quantity", e);
            return false;
        }
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        try {
            cartRepository.deleteByBuyerIdAndProductId(userId, productId);
            log.info("Removed item from cart for user: {}, product: {}", userId, productId);
        } catch (Exception e) {
            log.error("Error removing item from cart", e);
        }
    }
    
    @Transactional
    public void clearCart(Long userId) {
        try {
            cartRepository.deleteAllByBuyerId(userId);
            log.info("Cleared cart for user: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing cart", e);
        }
    }

    public BigDecimal getCartTotal(Long userId) {
        try {
            List<CartItem> cartItems = getCartItemsByUserId(userId);
            return cartItems.stream()
                    .map(CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            log.error("Error calculating cart total", e);
            return BigDecimal.ZERO;
        }
    }

    public int getCartItemCount(Long userId) {
        try {
            List<CartItem> cartItems = getCartItemsByUserId(userId);
            return cartItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
        } catch (Exception e) {
            log.error("Error calculating cart item count", e);
            return 0;
        }
    }

    public boolean validateCart(Long userId) {
        try {
            List<CartItem> cartItems = getCartItemsByUserId(userId);
            
            if (cartItems.isEmpty()) {
                log.info("Cart is empty for user: {}", userId);
                return false;
            }
            
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                
                // Check if product is still available
                if (product.getQuantity() < item.getQuantity()) {
                    log.warn("Cart validation failed - insufficient stock for product: {}", product.getName());
                    return false;
                }
                
                // Check if product is active
                if (!"ACTIVE".equals(product.getStatus())) {
                    log.warn("Cart validation failed - product not active: {}", product.getName());
                    return false;
                }
                
                // Check if product price has changed significantly
                BigDecimal currentPrice = product.getPrice();
                BigDecimal cartPrice = item.getTotalPrice().divide(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal priceDifference = currentPrice.subtract(cartPrice).abs();
                BigDecimal priceThreshold = currentPrice.multiply(BigDecimal.valueOf(0.1)); // 10% threshold
                
                if (priceDifference.compareTo(priceThreshold) > 0) {
                    log.warn("Cart validation failed - significant price change for product: {}", product.getName());
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error validating cart", e);
            return false;
        }
    }

    // New method: Get cart summary for quick display
    public CartSummary getCartSummary(Long userId) {
        try {
            List<CartItem> cartItems = getCartItemsByUserId(userId);
            BigDecimal total = getCartTotal(userId);
            int itemCount = getCartItemCount(userId);
            boolean isValid = validateCart(userId);
            
            return CartSummary.builder()
                    .itemCount(itemCount)
                    .totalAmount(total)
                    .isValid(isValid)
                    .hasItems(!cartItems.isEmpty())
                    .build();
        } catch (Exception e) {
            log.error("Error getting cart summary", e);
            return CartSummary.builder()
                    .itemCount(0)
                    .totalAmount(BigDecimal.ZERO)
                    .isValid(false)
                    .hasItems(false)
                    .build();
        }
    }

    // New method: Clean up expired cart items (older than 30 days)
    @Transactional
    public void cleanupExpiredCartItems() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            int deletedCount = cartRepository.deleteByUpdatedAtBefore(cutoffDate);
            log.info("Cleaned up {} expired cart items", deletedCount);
        } catch (Exception e) {
            log.error("Error cleaning up expired cart items", e);
        }
    }

    // New method: Merge guest cart with user cart
    @Transactional
    public boolean mergeGuestCart(Long userId, List<CartItem> guestCartItems) {
        try {
            for (CartItem guestItem : guestCartItems) {
                addToCart(userId, guestItem.getProduct().getId(), guestItem.getQuantity());
            }
            log.info("Successfully merged guest cart with user cart for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Error merging guest cart", e);
            return false;
        }
    }

    // Cart Summary DTO
    @lombok.Data
    @lombok.Builder
    public static class CartSummary {
        private int itemCount;
        private BigDecimal totalAmount;
        private boolean isValid;
        private boolean hasItems;
    }
} 