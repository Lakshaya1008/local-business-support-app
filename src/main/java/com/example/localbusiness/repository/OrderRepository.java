package com.example.localbusiness.repository;

import com.example.localbusiness.model.Order;
import com.example.localbusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findBySeller(User seller);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdOrderByOrderDateDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT o FROM Order o WHERE o.seller.id = :sellerId ORDER BY o.orderDate DESC")
    List<Order> findBySellerIdOrderByOrderDateDesc(@Param("sellerId") Long sellerId);
} 