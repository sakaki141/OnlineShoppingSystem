package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String category;
}