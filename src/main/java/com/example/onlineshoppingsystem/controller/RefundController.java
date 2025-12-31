package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.dto.ApplyRefundDTO;
import com.example.onlineshoppingsystem.dto.AuditRefundDTO;
import com.example.onlineshoppingsystem.entity.RefundApply;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refund")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @PostMapping("/apply")
    public Result<RefundApply> applyRefund(@RequestBody ApplyRefundDTO applyRefundDTO) {
        return refundService.applyRefund(
            applyRefundDTO.getOrderId(),
            applyRefundDTO.getItemId(),
            applyRefundDTO.getCustomerId(),
            applyRefundDTO.getReason()
        );
    }

    @GetMapping("/{refundId}")
    public Result<RefundApply> getRefundById(@PathVariable Integer refundId) {
        return refundService.getRefundById(refundId);
    }

    @GetMapping("/order/{orderId}")
    public Result<List<RefundApply>> getRefundsByOrderId(@PathVariable String orderId) {
        return refundService.getRefundsByOrderId(orderId);
    }

    @GetMapping("/customer/{customerId}")
    public Result<List<RefundApply>> getRefundsByCustomerId(@PathVariable Integer customerId) {
        return refundService.getRefundsByCustomerId(customerId);
    }

    @GetMapping("/seller/{sellerId}")
    public Result<List<RefundApply>> getRefundsBySellerId(@PathVariable Integer sellerId) {
        return refundService.getRefundsBySellerId(sellerId);
    }

    @GetMapping("/status/{status}")
    public Result<List<RefundApply>> getRefundsByStatus(@PathVariable Integer status) {
        return refundService.getRefundsByStatus(status);
    }

    @PutMapping("/audit")
    public Result<String> auditRefund(@RequestBody AuditRefundDTO auditRefundDTO) {
        return refundService.auditRefund(
            auditRefundDTO.getRefundId(),
            auditRefundDTO.getSellerId(),
            auditRefundDTO.getStatus(),
            auditRefundDTO.getRemark()
        );
    }

    @DeleteMapping("/cancel/{refundId}")
    public Result<String> cancelRefund(@PathVariable Integer refundId, 
                                      @RequestParam Integer customerId) {
        return refundService.cancelRefund(refundId, customerId);
    }

    @GetMapping("/order/{orderId}/item/{itemId}")
    public Result<List<RefundApply>> getRefundsByOrderIdAndItemId(@PathVariable String orderId,
                                                                 @PathVariable Integer itemId) {
        return refundService.getRefundsByOrderIdAndItemId(orderId, itemId);
    }
}