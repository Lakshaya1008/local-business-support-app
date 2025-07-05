package com.example.localbusiness.service;

import com.example.localbusiness.model.CartItem;
import com.example.localbusiness.model.Product;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.CartItemRepository;
import com.example.localbusiness.repository.ProductRepository;
import com.example.localbusiness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();
        CartItem item = cartItemRepository.findByUser(user).stream()
            .filter(ci -> ci.getProduct().getId().equals(productId))
            .findFirst().orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        cartItemRepository.save(item);
    }

    public void updateCartItem(User user, Long productId, int quantity) {
        CartItem item = cartItemRepository.findByUser(user).stream()
            .filter(ci -> ci.getProduct().getId().equals(productId))
            .findFirst().orElseThrow();
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    public void removeCartItem(User user, Long productId) {
        CartItem item = cartItemRepository.findByUser(user).stream()
            .filter(ci -> ci.getProduct().getId().equals(productId))
            .findFirst().orElseThrow();
        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
} 