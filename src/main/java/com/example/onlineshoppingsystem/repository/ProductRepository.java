package com.example.onlineshoppingsystem.repository;

import com.example.onlineshoppingsystem.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findBySellerId(Integer sellerId);
    
    long countBySellerId(Integer sellerId);
    
    List<Product> findByStatus(Integer status);
    
    Page<Product> findByStatusAndCategory(Integer status, String category, Pageable pageable);
    
    Page<Product> findByStatus(Integer status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 1 AND (p.productName LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByProductIdAndStatus(Integer productId, Integer status);
}