package com.example.localbusiness.controller;

import com.example.localbusiness.model.User;
import com.example.localbusiness.model.CartItem;
import com.example.localbusiness.service.CartService;
import com.example.localbusiness.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        return userRepository.findByEmail(email).orElse(null);
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        List<CartItem> cartItems = cartService.getCartItems(user);
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        User user = getCurrentUser(session);
        cartService.addToCart(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam Long productId, @RequestParam int quantity, HttpSession session) {
        User user = getCurrentUser(session);
        cartService.updateCartItem(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeCart(@RequestParam Long productId, HttpSession session) {
        User user = getCurrentUser(session);
        cartService.removeCartItem(user, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        User user = getCurrentUser(session);
        cartService.clearCart(user);
        return "redirect:/cart";
    }
} 