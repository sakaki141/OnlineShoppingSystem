package com.example.onlineshoppingsystem.service.impl;

import com.example.onlineshoppingsystem.entity.Product;
import com.example.onlineshoppingsystem.entity.ShoppingCart;
import com.example.onlineshoppingsystem.entity.User;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.repository.ProductRepository;
import com.example.onlineshoppingsystem.repository.ShoppingCartRepository;
import com.example.onlineshoppingsystem.repository.UserRepository;
import com.example.onlineshoppingsystem.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<ShoppingCart> addToCart(Integer customerId, Integer productId, Integer quantity) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        // 验证商品是否存在
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }

        Product product = productOptional.get();
        
        // 检查商品库存
        if (product.getStock() < quantity) {
            return Result.error("商品库存不足");
        }

        // 检查购物车中是否已存在该商品
        Optional<ShoppingCart> existingCartItem = shoppingCartRepository.findByCustomerIdAndProductId(customerId, productId);
        
        if (existingCartItem.isPresent()) {
            // 如果已存在，更新数量
            ShoppingCart cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            // 再次检查库存
            if (product.getStock() < newQuantity) {
                return Result.error("添加后商品库存不足");
            }
            
            cartItem.setQuantity(newQuantity);
            cartItem.setUpdateTime(LocalDateTime.now());
            ShoppingCart updatedCartItem = shoppingCartRepository.save(cartItem);
            return Result.success(updatedCartItem);
        } else {
            // 如果不存在，创建新的购物车项
            ShoppingCart newCartItem = new ShoppingCart();
            newCartItem.setCustomerId(customerId);
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(quantity);
            newCartItem.setCreateTime(LocalDateTime.now());
            newCartItem.setUpdateTime(LocalDateTime.now());
            
            ShoppingCart savedCartItem = shoppingCartRepository.save(newCartItem);
            return Result.success(savedCartItem);
        }
    }

    @Override
    public Result<ShoppingCart> updateCartItem(Integer cartId, Integer quantity) {
        // 验证购物车项是否存在
        Optional<ShoppingCart> cartItemOptional = shoppingCartRepository.findById(cartId);
        if (!cartItemOptional.isPresent()) {
            return Result.error("购物车项不存在");
        }

        ShoppingCart cartItem = cartItemOptional.get();
        
        // 验证商品是否存在
        Optional<Product> productOptional = productRepository.findById(cartItem.getProductId());
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }

        Product product = productOptional.get();
        
        // 检查商品库存
        if (product.getStock() < quantity) {
            return Result.error("商品库存不足");
        }

        // 更新购物车项数量
        cartItem.setQuantity(quantity);
        cartItem.setUpdateTime(LocalDateTime.now());
        
        ShoppingCart updatedCartItem = shoppingCartRepository.save(cartItem);
        return Result.success(updatedCartItem);
    }

    @Override
    public Result<String> removeFromCart(Integer cartId, Integer customerId) {
        // 验证购物车项是否存在
        Optional<ShoppingCart> cartItemOptional = shoppingCartRepository.findById(cartId);
        if (!cartItemOptional.isPresent()) {
            return Result.error("购物车项不存在");
        }

        ShoppingCart cartItem = cartItemOptional.get();
        
        // 验证是否为该用户的购物车项
        if (!cartItem.getCustomerId().equals(customerId)) {
            return Result.error("无权限删除他人购物车项");
        }

        shoppingCartRepository.deleteById(cartId);
        return Result.success("商品已从购物车移除");
    }

    @Override
    public Result<String> clearCart(Integer customerId) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        shoppingCartRepository.deleteByCustomerId(customerId);
        return Result.success("购物车已清空");
    }

    @Override
    public Result<List<ShoppingCart>> getCartItemsByCustomerId(Integer customerId) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        List<ShoppingCart> cartItems = shoppingCartRepository.findByCustomerId(customerId);
        return Result.success(cartItems);
    }

    @Override
    public Result<ShoppingCart> getCartItemById(Integer cartId) {
        Optional<ShoppingCart> cartItemOptional = shoppingCartRepository.findById(cartId);
        if (!cartItemOptional.isPresent()) {
            return Result.error("购物车项不存在");
        }
        return Result.success(cartItemOptional.get());
    }

    @Override
    public Result<String> batchRemoveFromCart(List<Integer> cartIds, Integer customerId) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        // 验证每个购物车项是否属于该用户并删除
        for (Integer cartId : cartIds) {
            Optional<ShoppingCart> cartItemOptional = shoppingCartRepository.findById(cartId);
            if (cartItemOptional.isPresent()) {
                ShoppingCart cartItem = cartItemOptional.get();
                if (!cartItem.getCustomerId().equals(customerId)) {
                    return Result.error("无权限删除他人购物车项: " + cartId);
                }
            } else {
                return Result.error("购物车项不存在: " + cartId);
            }
        }

        // 删除所有指定的购物车项
        shoppingCartRepository.deleteAllById(cartIds);
        return Result.success("批量删除购物车项成功");
    }
}