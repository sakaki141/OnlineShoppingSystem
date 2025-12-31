package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.dto.AddToCartDTO;
import com.example.onlineshoppingsystem.entity.ShoppingCart;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<ShoppingCart> addToCart(@RequestBody AddToCartDTO addToCartDTO) {
        return shoppingCartService.addToCart(
            addToCartDTO.getCustomerId(), 
            addToCartDTO.getProductId(), 
            addToCartDTO.getQuantity()
        );
    }

    @PutMapping("/update/{cartId}")
    public Result<ShoppingCart> updateCartItem(@PathVariable Integer cartId, 
                                              @RequestParam Integer quantity) {
        return shoppingCartService.updateCartItem(cartId, quantity);
    }

    @DeleteMapping("/remove/{cartId}")
    public Result<String> removeFromCart(@PathVariable Integer cartId, 
                                        @RequestParam Integer customerId) {
        return shoppingCartService.removeFromCart(cartId, customerId);
    }

    @DeleteMapping("/clear")
    public Result<String> clearCart(@RequestParam Integer customerId) {
        return shoppingCartService.clearCart(customerId);
    }

    @GetMapping("/customer/{customerId}")
    public Result<List<ShoppingCart>> getCartItemsByCustomerId(@PathVariable Integer customerId) {
        return shoppingCartService.getCartItemsByCustomerId(customerId);
    }

    @GetMapping("/{cartId}")
    public Result<ShoppingCart> getCartItemById(@PathVariable Integer cartId) {
        return shoppingCartService.getCartItemById(cartId);
    }

    @DeleteMapping("/batch-remove")
    public Result<String> batchRemoveFromCart(@RequestParam List<Integer> cartIds, 
                                             @RequestParam Integer customerId) {
        return shoppingCartService.batchRemoveFromCart(cartIds, customerId);
    }
}