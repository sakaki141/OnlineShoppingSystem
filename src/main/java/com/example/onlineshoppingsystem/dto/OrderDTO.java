package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private String orderId;
    private Integer customerId;
    private BigDecimal totalAmount;
    private Integer orderStatus; // 1-待支付，2-已支付，3-待收货，4-已收货，5-已退款
    private LocalDateTime payTime;
    private LocalDateTime receiveTime;
    private LocalDateTime refundTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<OrderItemDTO> orderItems;
}