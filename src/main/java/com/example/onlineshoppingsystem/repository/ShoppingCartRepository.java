package com.example.onlineshoppingsystem.repository;

import com.example.onlineshoppingsystem.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    List<ShoppingCart> findByCustomerId(Integer customerId);
    
    Optional<ShoppingCart> findByCustomerIdAndProductId(Integer customerId, Integer productId);
    
    @Modifying
    @Query("DELETE FROM ShoppingCart sc WHERE sc.customerId = :customerId AND sc.productId = :productId")
    void deleteByCustomerIdAndProductId(@Param("customerId") Integer customerId, @Param("productId") Integer productId);
    
    @Modifying
    @Query("DELETE FROM ShoppingCart sc WHERE sc.customerId = :customerId")
    void deleteByCustomerId(@Param("customerId") Integer customerId);
    
    boolean existsByCustomerIdAndProductId(Integer customerId, Integer productId);
}