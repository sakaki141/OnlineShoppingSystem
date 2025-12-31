package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundApplyDTO {
    private Integer refundId;
    private String orderId;
    private Integer itemId;
    private Integer customerId;
    private Integer sellerId;
    private BigDecimal refundAmount;
    private String applyReason;
    private Integer auditStatus; // 0-待审核，1-同意，2-拒绝
    private LocalDateTime auditTime;
    private String auditRemark;
    private LocalDateTime createTime;
}