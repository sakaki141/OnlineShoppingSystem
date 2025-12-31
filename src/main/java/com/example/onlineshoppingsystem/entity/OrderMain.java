package com.example.onlineshoppingsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_main")
public class OrderMain {
    @Id
    @Column(name = "order_id", length = 50)
    private String orderId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "order_status", nullable = false)
    private Integer orderStatus; // 0-待付款，1-待发货，2-待收货，3-已完成，4-已取消

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}