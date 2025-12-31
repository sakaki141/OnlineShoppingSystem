package com.example.onlineshoppingsystem.service.impl;

import com.example.onlineshoppingsystem.dto.SalesStatsDTO;
import com.example.onlineshoppingsystem.entity.*;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.repository.*;
import com.example.onlineshoppingsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMainRepository orderMainRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<OrderMain> createOrder(Integer customerId, List<Integer> cartItemIds) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        // 获取购物车项
        List<ShoppingCart> cartItems = shoppingCartRepository.findAllById(cartItemIds);
        if (cartItems.isEmpty()) {
            return Result.error("购物车项为空");
        }

        // 验证购物车项是否都属于该用户
        for (ShoppingCart cartItem : cartItems) {
            if (!cartItem.getCustomerId().equals(customerId)) {
                return Result.error("购物车项不属于该用户");
            }
        }

        // 验证商品库存并计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ShoppingCart cartItem : cartItems) {
            Optional<Product> productOptional = productRepository.findById(cartItem.getProductId());
            if (!productOptional.isPresent()) {
                return Result.error("商品不存在: " + cartItem.getProductId());
            }

            Product product = productOptional.get();
            if (product.getStock() < cartItem.getQuantity()) {
                return Result.error("商品库存不足: " + product.getProductName());
            }

            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }

        // 生成订单ID
        String orderId = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 创建主订单
        OrderMain orderMain = new OrderMain();
        orderMain.setOrderId(orderId);
        orderMain.setCustomerId(customerId);
        orderMain.setTotalAmount(totalAmount);
        orderMain.setOrderStatus(0); // 待支付
        orderMain.setCreateTime(LocalDateTime.now());
        orderMain.setUpdateTime(LocalDateTime.now());

        OrderMain savedOrderMain = orderMainRepository.save(orderMain);

        // 创建订单项并更新商品库存
        for (ShoppingCart cartItem : cartItems) {
            Optional<Product> productOptional = productRepository.findById(cartItem.getProductId());
            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setSellerId(product.getSellerId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(product.getPrice());
                orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));

                orderItemRepository.save(orderItem);

                // 更新商品库存
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
            }
        }

        // 清空购物车中的已下单商品
        shoppingCartRepository.deleteAll(cartItems);

        return Result.success(savedOrderMain);
    }

    @Override
    public Result<OrderMain> getOrderById(String orderId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }
        return Result.success(orderOptional.get());
    }

    @Override
    public Result<Page<OrderMain>> getOrdersByCustomerId(Integer customerId, int page, int size) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderMain> orders = orderMainRepository.findByCustomerId(customerId, pageable);
        return Result.success(orders);
    }

    @Override
    public Result<List<OrderItem>> getOrderItemsByOrderId(String orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return Result.success(orderItems);
    }

    @Override
    public Result<String> updateOrderStatus(String orderId, Integer status) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        orderMain.setOrderStatus(status);
        orderMain.setUpdateTime(LocalDateTime.now());

        // 根据状态设置相应的时间字段
        if (status == 1) { // 已支付
            orderMain.setPayTime(LocalDateTime.now());
        } else if (status == 3) { // 已收货
            orderMain.setReceiveTime(LocalDateTime.now());
        } else if (status == 5) { // 已退款
            orderMain.setRefundTime(LocalDateTime.now());
        }

        orderMainRepository.save(orderMain);
        return Result.success("订单状态更新成功");
    }

    @Override
    public Result<String> cancelOrder(String orderId, Integer customerId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证是否为该用户的订单
        if (!orderMain.getCustomerId().equals(customerId)) {
            return Result.error("无权限取消他人订单");
        }

        // 只有待支付的订单可以取消
        if (orderMain.getOrderStatus() != 0) {
            return Result.error("只有待支付订单可以取消");
        }

        // 恢复商品库存
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Optional<Product> productOptional = productRepository.findById(orderItem.getProductId());
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        orderMain.setOrderStatus(4); // 设置为已取消
        orderMain.setUpdateTime(LocalDateTime.now());
        orderMainRepository.save(orderMain);

        return Result.success("订单已取消");
    }

    @Override
    public Result<String> confirmReceipt(String orderId, Integer customerId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证是否为该用户的订单
        if (!orderMain.getCustomerId().equals(customerId)) {
            return Result.error("无权限确认他人订单收货");
        }

        // 只有待收货的订单可以确认收货
        if (orderMain.getOrderStatus() != 2) {
            return Result.error("订单状态不正确，无法确认收货");
        }

        orderMain.setOrderStatus(3); // 已完成
        orderMain.setReceiveTime(LocalDateTime.now());
        orderMain.setUpdateTime(LocalDateTime.now());
        orderMainRepository.save(orderMain);

        return Result.success("订单收货确认成功");
    }

    @Override
    public Result<List<OrderMain>> getOrdersByStatus(Integer status) {
        List<OrderMain> orders = orderMainRepository.findByOrderStatus(status);
        return Result.success(orders);
    }

    @Override
    public Result<String> deleteOrder(String orderId, Integer customerId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证是否为该用户的订单
        if (!orderMain.getCustomerId().equals(customerId)) {
            return Result.error("无权限删除他人订单");
        }

        // 只有已完成或已取消的订单可以删除
        if (orderMain.getOrderStatus() != 4 && orderMain.getOrderStatus() != 0) {
            return Result.error("只有已完成或已取消的订单可以删除");
        }

        // 删除订单项
        orderItemRepository.deleteByOrderId(orderId);
        
        // 删除主订单
        orderMainRepository.deleteById(orderId);

        return Result.success("订单删除成功");
    }

    @Override
    public Result<Page<OrderMain>> getOrdersByStatusAndCustomerId(Integer status, Integer customerId, int page, int size) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        // 为了正确实现分页，我们需要在Repository中添加适当的查询方法
        // 现在我们先获取所有符合条件的订单，然后手动分页
        List<OrderMain> allOrders = orderMainRepository.findByCustomerIdAndOrderStatus(customerId, status);
        int start = page * size;
        int end = Math.min(start + size, allOrders.size());
        
        if (start > allOrders.size()) {
            // 如果起始位置超出列表大小，返回空列表
            List<OrderMain> emptyList = List.of();
            Page<OrderMain> emptyPage = new PageImpl<>(emptyList, PageRequest.of(page, size), allOrders.size());
            return Result.success(emptyPage);
        }
        
        List<OrderMain> pagedOrders = allOrders.subList(start, end);
        Page<OrderMain> resultPage = new PageImpl<>(pagedOrders, PageRequest.of(page, size), allOrders.size());
        
        return Result.success(resultPage);
    }

    @Override
    public Result<List<OrderMain>> getCustomerOrdersInDateRange(Integer customerId, LocalDateTime startTime, LocalDateTime endTime) {
        // 验证用户是否存在
        Optional<User> customerOptional = userRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return Result.error("用户不存在");
        }

        List<OrderMain> orders = orderMainRepository.findByCustomerIdAndCreateTimeBetween(customerId, startTime, endTime);
        return Result.success(orders);
    }

    @Override
    public Result<Page<OrderMain>> getOrdersBySellerId(Integer sellerId, int page, int size) {
        try {
            // 获取该卖家所有的订单项
            List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
            
            if (orderItems.isEmpty()) {
                // 如果没有订单项，返回空分页
                Pageable pageable = PageRequest.of(page, size);
                Page<OrderMain> emptyPage = new PageImpl<>(List.of(), pageable, 0);
                return Result.success(emptyPage);
            }
            
            // 提取唯一的订单ID
            List<String> orderIds = orderItems.stream()
                    .map(OrderItem::getOrderId)
                    .distinct()
                    .toList();
            
            // 获取所有相关的订单
            List<OrderMain> allOrders = orderMainRepository.findAllById(orderIds);
            
            // 手动分页
            Pageable pageable = PageRequest.of(page, size);
            int start = page * size;
            int end = Math.min(start + size, allOrders.size());
            
            List<OrderMain> pagedOrders;
            if (start >= allOrders.size()) {
                pagedOrders = List.of();
            } else {
                pagedOrders = allOrders.subList(start, end);
            }
            
            Page<OrderMain> resultPage = new PageImpl<>(pagedOrders, pageable, allOrders.size());
            
            return Result.success(resultPage);
        } catch (Exception e) {
            return Result.error("获取卖家订单失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<SalesStatsDTO> getSalesStatsBySellerId(Integer sellerId) {
        try {
            // 获取该卖家所有的订单项
            List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
            
            if (orderItems.isEmpty()) {
                // 如果没有订单项，返回空统计数据
                SalesStatsDTO stats = new SalesStatsDTO();
                stats.setTotalSales(BigDecimal.ZERO);
                stats.setTotalOrders(0L);
                stats.setTotalCustomers(0L);
                stats.setTotalProducts(0L);
                stats.setSalesChange(0.0);
                stats.setCustomersChange(0.0);
                return Result.success(stats);
            }
            
            // 提取唯一的订单ID
            List<String> orderIds = orderItems.stream()
                    .map(OrderItem::getOrderId)
                    .distinct()
                    .toList();
            
            // 获取所有相关的订单
            List<OrderMain> allOrders = orderMainRepository.findAllById(orderIds);
            
            // 计算总销售额（只计算已支付的订单，排除已取消和已退款订单）
            BigDecimal totalSales = allOrders.stream()
                    .filter(order -> order.getOrderStatus() >= 1 && order.getOrderStatus() != 4 && order.getOrderStatus() != 5) // 已支付或更高状态，但排除已取消和已退款订单
                    .map(OrderMain::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算订单总数
            long totalOrders = allOrders.size();
            
            // 计算客户总数（去重）
            long totalCustomers = allOrders.stream()
                    .map(OrderMain::getCustomerId)
                    .distinct()
                    .count();
            
            // 计算商品总数
            long totalProducts = productRepository.countBySellerId(sellerId);
            
            // 计算销售额变化率（与上一个月相比）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thisMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);
            
            // 获取本月销售额
            BigDecimal thisMonthSales = calculateSalesInDateRange(sellerId, thisMonthStart, now);
            
            // 获取上月销售额
            BigDecimal lastMonthSales = calculateSalesInDateRange(sellerId, lastMonthStart, lastMonthEnd);
            
            // 计算销售额变化率
            double salesChange = 0.0;
            if (lastMonthSales.compareTo(BigDecimal.ZERO) > 0) {
                salesChange = (thisMonthSales.subtract(lastMonthSales).divide(lastMonthSales, 4, BigDecimal.ROUND_HALF_UP)).doubleValue() * 100;
            }
            
            // 计算客户变化率
            long thisMonthCustomers = calculateCustomersInDateRange(sellerId, thisMonthStart, now);
            long lastMonthCustomers = calculateCustomersInDateRange(sellerId, lastMonthStart, lastMonthEnd);
            
            double customersChange = 0.0;
            if (lastMonthCustomers > 0) {
                customersChange = ((double)(thisMonthCustomers - lastMonthCustomers) / lastMonthCustomers) * 100;
            }
            
            // 构建统计数据DTO
            SalesStatsDTO stats = new SalesStatsDTO();
            stats.setTotalSales(totalSales);
            stats.setTotalOrders(totalOrders);
            stats.setTotalCustomers(totalCustomers);
            stats.setTotalProducts(totalProducts);
            stats.setSalesChange(salesChange);
            stats.setCustomersChange(customersChange);
            
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取销售统计数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> payOrder(String orderId, Integer customerId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证是否为该用户的订单
        if (!orderMain.getCustomerId().equals(customerId)) {
            return Result.error("无权限支付他人订单");
        }

        // 只有待支付的订单可以支付
        if (orderMain.getOrderStatus() != 0) {
            return Result.error("只有待支付订单可以支付");
        }

        orderMain.setOrderStatus(1); // 改为待发货状态
        orderMain.setPayTime(LocalDateTime.now());
        orderMain.setUpdateTime(LocalDateTime.now());
        orderMainRepository.save(orderMain);

        return Result.success("订单支付成功");
    }

    @Override
    public Result<String> shipOrder(String orderId, Integer sellerId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证该订单是否包含该卖家的商品
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        boolean hasSellerProduct = orderItems.stream()
                .anyMatch(item -> item.getSellerId().equals(sellerId));
        
        if (!hasSellerProduct) {
            return Result.error("该订单不包含您的商品，无法操作");
        }

        // 只有待发货的订单可以发货
        if (orderMain.getOrderStatus() != 1) {
            return Result.error("只有待发货订单可以发货");
        }

        orderMain.setOrderStatus(2); // 改为待收货状态
        orderMain.setUpdateTime(LocalDateTime.now());
        orderMainRepository.save(orderMain);

        return Result.success("商品发货成功");
    }

    @Override
    public Result<String> confirmDelivery(String orderId, Integer sellerId) {
        Optional<OrderMain> orderOptional = orderMainRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return Result.error("订单不存在");
        }

        OrderMain orderMain = orderOptional.get();
        
        // 验证该订单是否包含该卖家的商品
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        boolean hasSellerProduct = orderItems.stream()
                .anyMatch(item -> item.getSellerId().equals(sellerId));
        
        if (!hasSellerProduct) {
            return Result.error("该订单不包含您的商品，无法操作");
        }

        // 只有待收货的订单可以确认送达
        if (orderMain.getOrderStatus() != 2) {
            return Result.error("只有待收货订单可以确认送达");
        }

        orderMain.setOrderStatus(3); // 改为已完成状态
        orderMain.setReceiveTime(LocalDateTime.now());
        orderMain.setUpdateTime(LocalDateTime.now());
        orderMainRepository.save(orderMain);

        return Result.success("订单确认送达成功");
    }
    
    /**
     * 计算指定日期范围内的销售额
     */
    private BigDecimal calculateSalesInDateRange(Integer sellerId, LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        if (orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        List<String> orderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .toList();
        
        List<OrderMain> orders = orderMainRepository.findAllById(orderIds);
        
        return orders.stream()
                .filter(order -> order.getOrderStatus() >= 1 && order.getOrderStatus() != 4 && order.getOrderStatus() != 5) // 已支付或更高状态，但排除已取消和已退款订单
                .filter(order -> order.getCreateTime().isAfter(startTime) && order.getCreateTime().isBefore(endTime))
                .map(OrderMain::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 计算指定日期范围内的客户数量
     */
    private long calculateCustomersInDateRange(Integer sellerId, LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        if (orderItems.isEmpty()) {
            return 0L;
        }
        
        List<String> orderIds = orderItems.stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .toList();
        
        List<OrderMain> orders = orderMainRepository.findAllById(orderIds);
        
        return orders.stream()
                .filter(order -> order.getOrderStatus() >= 1 && order.getOrderStatus() != 4 && order.getOrderStatus() != 5) // 已支付或更高状态，但排除已取消和已退款订单
                .filter(order -> order.getCreateTime().isAfter(startTime) && order.getCreateTime().isBefore(endTime))
                .map(OrderMain::getCustomerId)
                .distinct()
                .count();
    }
}