package com.example.localbusiness.controller;

import com.example.localbusiness.model.User;
import com.example.localbusiness.model.WishlistItem;
import com.example.localbusiness.service.WishlistService;
import com.example.localbusiness.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        return userRepository.findByEmail(email).orElse(null);
    }

    @GetMapping
    public String viewWishlist(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        List<WishlistItem> wishlistItems = wishlistService.getWishlistItems(user);
        model.addAttribute("wishlistItems", wishlistItems);
        return "wishlist";
    }

    @PostMapping("/add")
    public String addToWishlist(@RequestParam Long productId, HttpSession session) {
        User user = getCurrentUser(session);
        wishlistService.addToWishlist(user, productId);
        return "redirect:/wishlist";
    }

    @PostMapping("/remove")
    public String removeFromWishlist(@RequestParam Long productId, HttpSession session) {
        User user = getCurrentUser(session);
        wishlistService.removeFromWishlist(user, productId);
        return "redirect:/wishlist";
    }
} 