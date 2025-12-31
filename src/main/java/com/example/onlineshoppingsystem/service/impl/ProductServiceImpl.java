package com.example.onlineshoppingsystem.service.impl;

import com.example.onlineshoppingsystem.entity.Product;
import com.example.onlineshoppingsystem.entity.User;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.repository.ProductRepository;
import com.example.onlineshoppingsystem.repository.UserRepository;
import com.example.onlineshoppingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<Product> createProduct(Product product, Integer sellerId) {
        // 验证卖家是否存在
        Optional<User> sellerOptional = userRepository.findById(sellerId);
        if (!sellerOptional.isPresent()) {
            return Result.error("卖家不存在");
        }

        User seller = sellerOptional.get();
        // 验证用户角色是否为卖家
        if (!"seller".equals(seller.getRole())) {
            return Result.error("该用户不是卖家，无法创建商品");
        }

        // 设置商品基本信息
        product.setSellerId(sellerId);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        // 设置默认状态为上架
        if (product.getStatus() == null) {
            product.setStatus(1);
        }

        Product savedProduct = productRepository.save(product);
        return Result.success(savedProduct);
    }

    @Override
    public Result<Product> updateProduct(Product product, Integer sellerId) {
        // 验证商品是否存在
        Optional<Product> productOptional = productRepository.findById(product.getProductId());
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }

        Product existingProduct = productOptional.get();
        
        // 验证是否为该商品的卖家
        if (!existingProduct.getSellerId().equals(sellerId)) {
            return Result.error("无权限修改他人商品");
        }

        // 更新商品信息
        existingProduct.setProductName(product.getProductName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setUpdateTime(LocalDateTime.now());

        Product updatedProduct = productRepository.save(existingProduct);
        return Result.success(updatedProduct);
    }

    @Override
    public Result<String> deleteProduct(Integer productId, Integer sellerId) {
        // 验证商品是否存在
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }

        Product existingProduct = productOptional.get();
        
        // 验证是否为该商品的卖家
        if (!existingProduct.getSellerId().equals(sellerId)) {
            return Result.error("无权限删除他人商品");
        }

        productRepository.deleteById(productId);
        return Result.success("商品删除成功");
    }

    @Override
    public Result<Product> getProductById(Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }
        return Result.success(productOptional.get());
    }

    @Override
    public Result<Page<Product>> getProductList(Integer status, String category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按关键词搜索
            productPage = productRepository.findByKeyword(keyword, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            // 按状态和分类搜索
            if (status != null) {
                productPage = productRepository.findByStatusAndCategory(status, category, pageable);
            } else {
                // 只按分类搜索，状态默认为上架（1）
                productPage = productRepository.findByStatusAndCategory(1, category, pageable);
            }
        } else if (status != null) {
            // 只按状态搜索
            productPage = productRepository.findByStatus(status, pageable);
        } else {
            // 不按状态和分类搜索，只按上架商品搜索
            productPage = productRepository.findByStatus(1, pageable);
        }

        return Result.success(productPage);
    }

    @Override
    public Result<List<Product>> getProductsBySellerId(Integer sellerId) {
        List<Product> products = productRepository.findBySellerId(sellerId);
        return Result.success(products);
    }

    @Override
    public Result<String> updateProductStock(Integer productId, Integer newStock) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }

        Product product = productOptional.get();
        product.setStock(newStock);
        product.setUpdateTime(LocalDateTime.now());
        
        productRepository.save(product);
        return Result.success("商品库存更新成功");
    }

    @Override
    public Result<String> updateProductStatus(Integer productId, Integer status) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            return Result.error("商品不存在");
        }

        Product product = productOptional.get();
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        
        productRepository.save(product);
        return Result.success("商品状态更新成功");
    }
}