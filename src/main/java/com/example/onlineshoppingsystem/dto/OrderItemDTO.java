package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Integer itemId;
    private String orderId;
    private Integer productId;
    private Integer sellerId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}