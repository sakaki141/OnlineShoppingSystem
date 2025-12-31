package com.example.onlineshoppingsystem.service;

import com.example.onlineshoppingsystem.entity.ShoppingCart;
import com.example.onlineshoppingsystem.exception.Result;
import java.util.List;

public interface ShoppingCartService {
    Result<ShoppingCart> addToCart(Integer customerId, Integer productId, Integer quantity);
    Result<ShoppingCart> updateCartItem(Integer cartId, Integer quantity);
    Result<String> removeFromCart(Integer cartId, Integer customerId);
    Result<String> clearCart(Integer customerId);
    Result<List<ShoppingCart>> getCartItemsByCustomerId(Integer customerId);
    Result<ShoppingCart> getCartItemById(Integer cartId);
    Result<String> batchRemoveFromCart(List<Integer> cartIds, Integer customerId);
}