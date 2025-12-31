package com.example.onlineshoppingsystem.repository;

import com.example.onlineshoppingsystem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(String orderId);
    
    List<OrderItem> findByProductId(Integer productId);
    
    List<OrderItem> findBySellerId(Integer sellerId);

    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.orderId = ?1")
    void deleteByOrderId(String orderId);
}