package com.example.onlineshoppingsystem.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}