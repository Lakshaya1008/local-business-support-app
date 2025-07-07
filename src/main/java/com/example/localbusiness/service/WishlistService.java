package com.example.localbusiness.service;

import com.example.localbusiness.model.Product;
import com.example.localbusiness.model.User;
import com.example.localbusiness.model.Wishlist;
import com.example.localbusiness.repository.ProductRepository;
import com.example.localbusiness.repository.UserRepository;
import com.example.localbusiness.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Transactional
    public boolean addToWishlist(Long userId, Long productId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (userOpt.isEmpty() || productOpt.isEmpty()) {
                log.warn("User or product not found - UserId: {}, ProductId: {}", userId, productId);
                return false;
            }
            
            User user = userOpt.get();
            Product product = productOpt.get();
            
            // Check if already in wishlist
            if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
                log.info("Product already in wishlist for user: {}, product: {}", userId, productId);
                return true; // Already exists, consider it successful
            }
            
            // Check if product is active
            if (!"ACTIVE".equals(product.getStatus())) {
                log.warn("Product is not active: {}", product.getName());
                return false;
            }
            
            Wishlist wishlist = new Wishlist(user, product);
            wishlistRepository.save(wishlist);
            
            log.info("Added product to wishlist for user: {}, product: {}", userId, productId);
            return true;
        } catch (Exception e) {
            log.error("Error adding product to wishlist", e);
            return false;
        }
    }

    @Transactional
    public boolean removeFromWishlist(Long userId, Long productId) {
        try {
            if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
                log.warn("Product not in wishlist for user: {}, product: {}", userId, productId);
                return false;
            }
            
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            log.info("Removed product from wishlist for user: {}, product: {}", userId, productId);
            return true;
        } catch (Exception e) {
            log.error("Error removing product from wishlist", e);
            return false;
        }
    }

    public List<Wishlist> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    public long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    @Transactional
    public boolean moveToCart(Long userId, Long productId, Integer quantity) {
        try {
            // First add to cart
            boolean addedToCart = cartService.addToCart(userId, productId, quantity);
            
            if (addedToCart) {
                // Then remove from wishlist
                removeFromWishlist(userId, productId);
                log.info("Successfully moved product from wishlist to cart for user: {}, product: {}", userId, productId);
                return true;
            } else {
                log.warn("Failed to add product to cart, not removing from wishlist");
                return false;
            }
        } catch (Exception e) {
            log.error("Error moving product from wishlist to cart", e);
            return false;
        }
    }

    @Transactional
    public boolean moveAllToCart(Long userId) {
        try {
            List<Wishlist> wishlistItems = getUserWishlist(userId);
            boolean allMoved = true;
            
            for (Wishlist item : wishlistItems) {
                boolean moved = moveToCart(userId, item.getProduct().getId(), 1);
                if (!moved) {
                    allMoved = false;
                }
            }
            
            if (allMoved) {
                log.info("Successfully moved all items from wishlist to cart for user: {}", userId);
            } else {
                log.warn("Some items failed to move from wishlist to cart for user: {}", userId);
            }
            
            return allMoved;
        } catch (Exception e) {
            log.error("Error moving all items from wishlist to cart", e);
            return false;
        }
    }

    @Transactional
    public void clearWishlist(Long userId) {
        try {
            wishlistRepository.deleteAllByUserId(userId);
            log.info("Cleared wishlist for user: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing wishlist", e);
        }
    }

    public List<Long> getWishlistProductIds(Long userId) {
        return wishlistRepository.findProductIdsByUserId(userId);
    }

    // Get wishlist summary for quick display
    public WishlistSummary getWishlistSummary(Long userId) {
        try {
            List<Wishlist> wishlistItems = getUserWishlist(userId);
            long count = getWishlistCount(userId);
            
            return WishlistSummary.builder()
                    .itemCount((int) count)
                    .hasItems(!wishlistItems.isEmpty())
                    .build();
        } catch (Exception e) {
            log.error("Error getting wishlist summary", e);
            return WishlistSummary.builder()
                    .itemCount(0)
                    .hasItems(false)
                    .build();
        }
    }

    // Wishlist Summary DTO
    @lombok.Data
    @lombok.Builder
    public static class WishlistSummary {
        private int itemCount;
        private boolean hasItems;
    }
} 