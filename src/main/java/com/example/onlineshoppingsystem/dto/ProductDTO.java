package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Integer productId;
    private Integer sellerId;
    private String productName;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String category;
    private Integer status; // 0-下架，1-上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}