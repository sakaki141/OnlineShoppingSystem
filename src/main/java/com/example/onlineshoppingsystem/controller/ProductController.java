package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.dto.ProductCreateDTO;
import com.example.onlineshoppingsystem.dto.ProductUpdateDTO;
import com.example.onlineshoppingsystem.entity.Product;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public Result<Product> createProduct(@RequestBody ProductCreateDTO productCreateDTO, 
                                         @RequestParam Integer sellerId) {
        Product product = new Product();
        product.setProductName(productCreateDTO.getProductName());
        product.setPrice(productCreateDTO.getPrice());
        product.setStock(productCreateDTO.getStock());
        product.setDescription(productCreateDTO.getDescription());
        product.setCategory(productCreateDTO.getCategory());
        
        return productService.createProduct(product, sellerId);
    }

    @PutMapping("/update")
    public Result<Product> updateProduct(@RequestBody ProductUpdateDTO productUpdateDTO, 
                                         @RequestParam Integer sellerId) {
        Product product = new Product();
        product.setProductId(productUpdateDTO.getProductId());
        product.setProductName(productUpdateDTO.getProductName());
        product.setPrice(productUpdateDTO.getPrice());
        product.setStock(productUpdateDTO.getStock());
        product.setDescription(productUpdateDTO.getDescription());
        product.setCategory(productUpdateDTO.getCategory());
        
        return productService.updateProduct(product, sellerId);
    }

    @DeleteMapping("/delete/{productId}")
    public Result<String> deleteProduct(@PathVariable Integer productId, 
                                        @RequestParam Integer sellerId) {
        return productService.deleteProduct(productId, sellerId);
    }

    @GetMapping("/{productId}")
    public Result<Product> getProductById(@PathVariable Integer productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/list")
    public Result<Page<Product>> getProductList(@RequestParam(required = false) Integer status,
                                                @RequestParam(required = false) String category,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return productService.getProductList(status, category, keyword, page, size);
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<Product>> getProductsBySellerId(@PathVariable Integer sellerId) {
        return productService.getProductsBySellerId(sellerId);
    }

    @PutMapping("/stock/{productId}")
    public Result<String> updateProductStock(@PathVariable Integer productId, 
                                             @RequestParam Integer newStock) {
        return productService.updateProductStock(productId, newStock);
    }

    @PutMapping("/status/{productId}")
    public Result<String> updateProductStatus(@PathVariable Integer productId, 
                                              @RequestParam Integer status) {
        return productService.updateProductStatus(productId, status);
    }
}