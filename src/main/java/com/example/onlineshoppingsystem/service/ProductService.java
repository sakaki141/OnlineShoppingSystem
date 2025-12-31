package com.example.onlineshoppingsystem.service;

import com.example.onlineshoppingsystem.entity.Product;
import com.example.onlineshoppingsystem.exception.Result;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Result<Product> createProduct(Product product, Integer sellerId);
    Result<Product> updateProduct(Product product, Integer sellerId);
    Result<String> deleteProduct(Integer productId, Integer sellerId);
    Result<Product> getProductById(Integer productId);
    Result<Page<Product>> getProductList(Integer status, String category, String keyword, int page, int size);
    Result<List<Product>> getProductsBySellerId(Integer sellerId);
    Result<String> updateProductStock(Integer productId, Integer newStock);
    Result<String> updateProductStatus(Integer productId, Integer status);
}