package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.dto.CreateOrderDTO;
import com.example.onlineshoppingsystem.dto.SalesStatsDTO;
import com.example.onlineshoppingsystem.entity.OrderItem;
import com.example.onlineshoppingsystem.entity.OrderMain;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<OrderMain> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO.getCustomerId(), createOrderDTO.getCartItemIds());
    }

    @GetMapping("/{orderId}")
    public Result<OrderMain> getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/customer/{customerId}")
    public Result<Page<OrderMain>> getOrdersByCustomerId(@PathVariable Integer customerId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByCustomerId(customerId, page, size);
    }

    @GetMapping("/items/{orderId}")
    public Result<List<OrderItem>> getOrderItemsByOrderId(@PathVariable String orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }

    @PutMapping("/status/{orderId}")
    public Result<String> updateOrderStatus(@PathVariable String orderId, 
                                           @RequestParam Integer status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @PutMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable String orderId, 
                                     @RequestParam Integer customerId) {
        return orderService.cancelOrder(orderId, customerId);
    }

    @PutMapping("/confirm-receipt/{orderId}")
    public Result<String> confirmReceipt(@PathVariable String orderId, 
                                        @RequestParam Integer customerId) {
        return orderService.confirmReceipt(orderId, customerId);
    }

    @GetMapping("/status/{status}")
    public Result<List<OrderMain>> getOrdersByStatus(@PathVariable Integer status) {
        return orderService.getOrdersByStatus(status);
    }

    @DeleteMapping("/delete/{orderId}")
    public Result<String> deleteOrder(@PathVariable String orderId, 
                                     @RequestParam Integer customerId) {
        return orderService.deleteOrder(orderId, customerId);
    }

    @GetMapping("/status/{status}/customer/{customerId}")
    public Result<Page<OrderMain>> getOrdersByStatusAndCustomerId(@PathVariable Integer status,
                                                                 @PathVariable Integer customerId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByStatusAndCustomerId(status, customerId, page, size);
    }

    @GetMapping("/customer/{customerId}/date-range")
    public Result<List<OrderMain>> getCustomerOrdersInDateRange(@PathVariable Integer customerId,
                                                               @RequestParam String startTime,
                                                               @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        return orderService.getCustomerOrdersInDateRange(customerId, start, end);
    }
    
    /**
     * 获取卖家相关的订单
     */
    @GetMapping("/seller/{sellerId}")
    public Result<Page<OrderMain>> getOrdersBySellerId(@PathVariable Integer sellerId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersBySellerId(sellerId, page, size);
    }
    
    /**
     * 获取卖家的销售统计数据
     */
    @GetMapping("/seller/{sellerId}/stats")
    public Result<SalesStatsDTO> getSalesStatsBySellerId(@PathVariable Integer sellerId) {
        return orderService.getSalesStatsBySellerId(sellerId);
    }

    @PutMapping("/pay/{orderId}")
    public Result<String> payOrder(@PathVariable String orderId,
                                 @RequestParam Integer customerId) {
        return orderService.payOrder(orderId, customerId);
    }

    @PutMapping("/ship/{orderId}")
    public Result<String> shipOrder(@PathVariable String orderId,
                                  @RequestParam Integer sellerId) {
        return orderService.shipOrder(orderId, sellerId);
    }

    @PutMapping("/delivery/{orderId}")
    public Result<String> confirmDelivery(@PathVariable String orderId,
                                       @RequestParam Integer sellerId) {
        return orderService.confirmDelivery(orderId, sellerId);
    }
}