package com.example.onlineshoppingsystem.service;

import com.example.onlineshoppingsystem.entity.RefundApply;
import com.example.onlineshoppingsystem.exception.Result;
import java.util.List;

public interface RefundService {
    Result<RefundApply> applyRefund(String orderId, Integer itemId, Integer customerId, String reason);
    Result<RefundApply> getRefundById(Integer refundId);
    Result<List<RefundApply>> getRefundsByOrderId(String orderId);
    Result<List<RefundApply>> getRefundsByCustomerId(Integer customerId);
    Result<List<RefundApply>> getRefundsBySellerId(Integer sellerId);
    Result<List<RefundApply>> getRefundsByStatus(Integer status);
    Result<String> auditRefund(Integer refundId, Integer sellerId, Integer status, String remark);
    Result<String> cancelRefund(Integer refundId, Integer customerId);
    Result<List<RefundApply>> getRefundsByOrderIdAndItemId(String orderId, Integer itemId);
}