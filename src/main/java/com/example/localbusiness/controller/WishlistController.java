package com.example.localbusiness.controller;

import com.example.localbusiness.model.Wishlist;
import com.example.localbusiness.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    
    private final WishlistService wishlistService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(
            @PathVariable Long productId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean success = wishlistService.addToWishlist(userId, productId);
        
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "Added to wishlist" : "Failed to add to wishlist"
        ));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean success = wishlistService.removeFromWishlist(userId, productId);
        
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "Removed from wishlist" : "Failed to remove from wishlist"
        ));
    }

    @GetMapping
    public ResponseEntity<List<Wishlist>> getUserWishlist(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<Wishlist> wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getWishlistCount(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        long count = wishlistService.getWishlistCount(userId);
        
        return ResponseEntity.ok(Map.of(
            "count", count
        ));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkWishlistStatus(
            @PathVariable Long productId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean isInWishlist = wishlistService.isInWishlist(userId, productId);
        
        return ResponseEntity.ok(Map.of(
            "inWishlist", isInWishlist
        ));
    }

    @PostMapping("/move-to-cart/{productId}")
    public ResponseEntity<Map<String, Object>> moveToCart(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean success = wishlistService.moveToCart(userId, productId, quantity);
        
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "Moved to cart" : "Failed to move to cart"
        ));
    }

    @PostMapping("/move-all-to-cart")
    public ResponseEntity<Map<String, Object>> moveAllToCart(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean success = wishlistService.moveAllToCart(userId);
        
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? "All items moved to cart" : "Failed to move all items to cart"
        ));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearWishlist(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        wishlistService.clearWishlist(userId);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Wishlist cleared"
        ));
    }

    @GetMapping("/summary")
    public ResponseEntity<WishlistService.WishlistSummary> getWishlistSummary(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        WishlistService.WishlistSummary summary = wishlistService.getWishlistSummary(userId);
        return ResponseEntity.ok(summary);
    }
} 