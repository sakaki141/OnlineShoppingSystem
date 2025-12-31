package com.example.onlineshoppingsystem.service;

import com.example.onlineshoppingsystem.entity.OrderMain;
import com.example.onlineshoppingsystem.dto.SalesStatsDTO;
import com.example.onlineshoppingsystem.entity.OrderItem;
import com.example.onlineshoppingsystem.exception.Result;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Result<OrderMain> createOrder(Integer customerId, List<Integer> cartItemIds);
    Result<OrderMain> getOrderById(String orderId);
    Result<Page<OrderMain>> getOrdersByCustomerId(Integer customerId, int page, int size);
    Result<List<OrderItem>> getOrderItemsByOrderId(String orderId);
    Result<String> updateOrderStatus(String orderId, Integer status);
    Result<String> cancelOrder(String orderId, Integer customerId);
    Result<String> confirmReceipt(String orderId, Integer customerId);
    Result<List<OrderMain>> getOrdersByStatus(Integer status);
    Result<String> deleteOrder(String orderId, Integer customerId);
    Result<Page<OrderMain>> getOrdersByStatusAndCustomerId(Integer status, Integer customerId, int page, int size);
    Result<List<OrderMain>> getCustomerOrdersInDateRange(Integer customerId, LocalDateTime startTime, LocalDateTime endTime);
    Result<Page<OrderMain>> getOrdersBySellerId(Integer sellerId, int page, int size);
    
    /**
     * 获取卖家的销售统计数据
     */
    Result<SalesStatsDTO> getSalesStatsBySellerId(Integer sellerId);

    /**
     * 用户支付订单
     */
    Result<String> payOrder(String orderId, Integer customerId);

    /**
     * 卖家发货
     */
    Result<String> shipOrder(String orderId, Integer sellerId);

    /**
     * 卖家确认送达
     */
    Result<String> confirmDelivery(String orderId, Integer sellerId);
}