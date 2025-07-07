package com.example.localbusiness.repository;

import com.example.localbusiness.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByBuyerId(Long userId);
    
    List<CartItem> findByBuyerIdOrderByCreatedAtDesc(Long userId);
    
    Optional<CartItem> findByBuyerIdAndProductId(Long userId, Long productId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.buyer.id = :userId AND c.product.id = :productId")
    void deleteByBuyerIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.buyer.id = :userId")
    void deleteAllByBuyerId(@Param("userId") Long userId);
    
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.updatedAt < :cutoffDate")
    int deleteByUpdatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.buyer.id = :userId")
    long countByBuyerId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.buyer.id = :userId")
    Integer getTotalItemCountByBuyerId(@Param("userId") Long userId);
    
    boolean existsByBuyerIdAndProductId(Long userId, Long productId);
} 