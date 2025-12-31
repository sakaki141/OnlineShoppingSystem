package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    private String productName;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String category;
}