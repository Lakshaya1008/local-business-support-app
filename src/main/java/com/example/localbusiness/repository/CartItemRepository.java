package com.example.localbusiness.repository;

import com.example.localbusiness.model.CartItem;
import com.example.localbusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    void deleteByUser(User user);
} 