package com.example.localbusiness.controller;

import com.example.localbusiness.dto.AuthRequest;
import com.example.localbusiness.dto.AuthResponse;
import com.example.localbusiness.dto.RegisterRequest;
import com.example.localbusiness.dto.ProductRequest;
import com.example.localbusiness.service.AuthService;
import com.example.localbusiness.service.ProductService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest request, 
                             RedirectAttributes redirectAttributes) {
        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        try {
            AuthRequest request = new AuthRequest();
            request.setEmail(email);
            request.setPassword(password);
            AuthResponse response = authService.authenticate(request);
            session.setAttribute("token", response.getToken());
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            String msg = (e.getMessage() == null || e.getMessage().isEmpty()) ? "Invalid email or password." : e.getMessage();
            model.addAttribute("error", "Login failed: " + msg);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        String userEmail = (String) session.getAttribute("userEmail");
        model.addAttribute("userEmail", userEmail);
        return "dashboard";
    }

    @GetMapping("/products")
    public String products(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        try {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("userEmail", session.getAttribute("userEmail"));
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load products: " + e.getMessage());
        }
        return "products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute ProductRequest request,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        try {
            productService.addProduct(request);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/login";
        }
        
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/products";
    }
} 