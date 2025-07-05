package com.example.localbusiness.controller;

import com.example.localbusiness.dto.ReviewRequest;
import com.example.localbusiness.model.Review;
import com.example.localbusiness.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest request, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Review review = reviewService.createReview(request, userId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Review>> getUserReviews(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<Review> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{reviewId}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable Long reviewId) {
        Review review = reviewService.approveReview(reviewId);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/{reviewId}/reject")
    public ResponseEntity<Review> rejectReview(@PathVariable Long reviewId) {
        Review review = reviewService.rejectReview(reviewId);
        return ResponseEntity.ok(review);
    }
} 