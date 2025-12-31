package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDTO {
    private Integer customerId;
    private List<Integer> cartItemIds;
}