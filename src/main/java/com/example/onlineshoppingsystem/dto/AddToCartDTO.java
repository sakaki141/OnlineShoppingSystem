package com.example.onlineshoppingsystem.dto;

import lombok.Data;

@Data
public class AddToCartDTO {
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
}