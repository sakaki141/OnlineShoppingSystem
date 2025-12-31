package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShoppingCartDTO {
    private Integer cartId;
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}