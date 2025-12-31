package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesStatsDTO {
    private BigDecimal totalSales;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
    private double salesChange;
    private double customersChange;
}