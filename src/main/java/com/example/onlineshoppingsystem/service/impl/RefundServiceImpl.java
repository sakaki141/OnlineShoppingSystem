package com.example.onlineshoppingsystem.service.impl;

import com.example.onlineshoppingsystem.entity.*;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.repository.*;
import com.example.onlineshoppingsystem.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundApplyRepository refundApplyRepository;

    @Autowired
    private OrderMainRepository orderMainRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<RefundApply> applyRefund(String orderId, Integer itemId, Integer customerId, String reason) {
        // 验证订单是否存在
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证订单是否属于该用户
        if (!orderMain.getCustomerId().equals(customerId)) {
            return Result.error("无权限申请退款，订单不属于该用户");
        }

        // 验证订单状态是否允许退款（已支付或待收货状态）
        if (orderMain.getOrderStatus() != 2 && orderMain.getOrderStatus() != 3) {
            return Result.error("订单状态不允许退款");
        }

        // 验证订单项是否存在
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(itemId);
        if (!orderItemOptional.isPresent()) {
            return Result.error("订单项不存在");
        }

        OrderItem orderItem = orderItemOptional.get();
        
        // 验证订单项是否属于该订单
        if (!orderItem.getOrderId().equals(orderId)) {
            return Result.error("订单项不属于该订单");
        }

        // 创建退款申请
        RefundApply refundApply = new RefundApply();
        refundApply.setOrderId(orderId);
        refundApply.setItemId(itemId);
        refundApply.setCustomerId(customerId);
        refundApply.setSellerId(orderItem.getSellerId());
        refundApply.setRefundAmount(orderItem.getSubtotal()); // 退款金额为订单项小计
        refundApply.setApplyReason(reason);
        refundApply.setAuditStatus(0); // 待审核
        refundApply.setCreateTime(LocalDateTime.now());

        RefundApply savedRefundApply = refundApplyRepository.save(refundApply);
        return Result.success(savedRefundApply);
    }

    @Override
    public Result<RefundApply> getRefundById(Integer refundId) {
        Optional<RefundApply> refundOptional = refundApplyRepository.findById(refundId);
        if (!refundOptional.isPresent()) {
            return Result.error("退款申请不存在");
        }
        return Result.success(refundOptional.get());
    }

    @Override
    public Result<List<RefundApply>> getRefundsByOrderId(String orderId) {
        List<RefundApply> refunds = refundApplyRepository.findByOrderId(orderId);
        return Result.success(refunds);
    }

    @Override
    public Result<List<RefundApply>> getRefundsByCustomerId(Integer customerId) {
        List<RefundApply> refunds = refundApplyRepository.findByCustomerId(customerId);
        return Result.success(refunds);
    }

    @Override
    public Result<List<RefundApply>> getRefundsBySellerId(Integer sellerId) {
        List<RefundApply> refunds = refundApplyRepository.findBySellerId(sellerId);
        return Result.success(refunds);
    }

    @Override
    public Result<List<RefundApply>> getRefundsByStatus(Integer status) {
        List<RefundApply> refunds = refundApplyRepository.findByAuditStatus(status);
        return Result.success(refunds);
    }

    @Override
    public Result<String> auditRefund(Integer refundId, Integer sellerId, Integer status, String remark) {
        // 验证退款申请是否存在
        Optional<RefundApply> refundOptional = refundApplyRepository.findById(refundId);
        if (!refundOptional.isPresent()) {
            return Result.error("退款申请不存在");
        }

        RefundApply refundApply = refundOptional.get();
        
        // 验证是否为该退款申请的卖家
        if (!refundApply.getSellerId().equals(sellerId)) {
            return Result.error("无权限审核他人退款申请");
        }

        // 验证审核状态是否有效（1-同意，2-拒绝）
        if (status != 1 && status != 2) {
            return Result.error("无效的审核状态");
        }

        // 更新退款申请状态
        refundApply.setAuditStatus(status);
        refundApply.setAuditTime(LocalDateTime.now());
        refundApply.setAuditRemark(remark);

        refundApplyRepository.save(refundApply);

        // 如果审核通过，需要更新订单状态
        if (status == 1) { // 同意退款
            // 更新订单状态为已退款
            Optional<OrderMain> orderOptional = orderMainRepository.findById(refundApply.getOrderId());
            if (orderOptional.isPresent()) {
                OrderMain orderMain = orderOptional.get();
                orderMain.setOrderStatus(5); // 已退款
                orderMain.setRefundTime(LocalDateTime.now());
                orderMain.setUpdateTime(LocalDateTime.now());
                orderMainRepository.save(orderMain);
            }

            // 恢复商品库存
            Optional<OrderItem> orderItemOptional = orderItemRepository.findById(refundApply.getItemId());
            if (orderItemOptional.isPresent()) {
                OrderItem orderItem = orderItemOptional.get();
                Optional<Product> productOptional = productRepository.findById(orderItem.getProductId());
                if (productOptional.isPresent()) {
                    Product product = productOptional.get();
                    product.setStock(product.getStock() + orderItem.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        String statusText = status == 1 ? "同意" : "拒绝";
        return Result.success("退款申请审核" + statusText + "成功");
    }

    @Override
    public Result<String> cancelRefund(Integer refundId, Integer customerId) {
        // 验证退款申请是否存在
        Optional<RefundApply> refundOptional = refundApplyRepository.findById(refundId);
        if (!refundOptional.isPresent()) {
            return Result.error("退款申请不存在");
        }

        RefundApply refundApply = refundOptional.get();
        
        // 验证是否为该退款申请的用户
        if (!refundApply.getCustomerId().equals(customerId)) {
            return Result.error("无权限取消他人退款申请");
        }

        // 只有待审核的退款申请可以取消
        if (refundApply.getAuditStatus() != 0) {
            return Result.error("只有待审核的退款申请可以取消");
        }

        refundApplyRepository.deleteById(refundId);
        return Result.success("退款申请已取消");
    }

    @Override
    public Result<List<RefundApply>> getRefundsByOrderIdAndItemId(String orderId, Integer itemId) {
        List<RefundApply> refunds = refundApplyRepository.findByOrderIdAndItemId(orderId, itemId);
        return Result.success(refunds);
    }
}