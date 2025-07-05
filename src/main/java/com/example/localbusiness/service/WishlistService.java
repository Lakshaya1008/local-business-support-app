package com.example.localbusiness.service;

import com.example.localbusiness.model.WishlistItem;
import com.example.localbusiness.model.Product;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.WishlistItemRepository;
import com.example.localbusiness.repository.ProductRepository;
import com.example.localbusiness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WishlistService {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public List<WishlistItem> getWishlistItems(User user) {
        return wishlistItemRepository.findByUser(user);
    }

    public void addToWishlist(User user, Long productId) {
        if (wishlistItemRepository.existsByUserAndProductId(user, productId)) return;
        Product product = productRepository.findById(productId).orElseThrow();
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        wishlistItemRepository.save(item);
    }

    public void removeFromWishlist(User user, Long productId) {
        wishlistItemRepository.deleteByUserAndProductId(user, productId);
    }
} 