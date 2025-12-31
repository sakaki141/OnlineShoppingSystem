package com.example.onlineshoppingsystem.dto;

import lombok.Data;

@Data
public class ApplyRefundDTO {
    private String orderId;
    private Integer itemId;
    private Integer customerId;
    private String reason;
}