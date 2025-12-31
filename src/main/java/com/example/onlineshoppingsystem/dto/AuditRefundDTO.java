package com.example.onlineshoppingsystem.dto;

import lombok.Data;

@Data
public class AuditRefundDTO {
    private Integer refundId;
    private Integer sellerId;
    private Integer status; // 1-同意，2-拒绝
    private String remark;
}