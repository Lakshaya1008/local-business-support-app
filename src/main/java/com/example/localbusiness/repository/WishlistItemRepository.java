package com.example.localbusiness.repository;

import com.example.localbusiness.model.WishlistItem;
import com.example.localbusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUser(User user);
    void deleteByUserAndProductId(User user, Long productId);
    boolean existsByUserAndProductId(User user, Long productId);
} 