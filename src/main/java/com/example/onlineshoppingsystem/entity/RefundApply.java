package com.example.onlineshoppingsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "refund_apply")
public class RefundApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Integer refundId;

    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "apply_reason", columnDefinition = "TEXT")
    private String applyReason;

    @Column(name = "audit_status")
    private Integer auditStatus = 0; // 0-待审核，1-同意，2-拒绝

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "audit_remark", columnDefinition = "TEXT")
    private String auditRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}