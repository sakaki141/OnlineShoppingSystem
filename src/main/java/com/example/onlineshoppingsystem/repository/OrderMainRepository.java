package com.example.onlineshoppingsystem.repository;

import com.example.onlineshoppingsystem.entity.OrderMain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderMainRepository extends JpaRepository<OrderMain, String> {
    List<OrderMain> findByCustomerId(Integer customerId);
    
    Page<OrderMain> findByCustomerId(Integer customerId, Pageable pageable);
    
    List<OrderMain> findByCustomerIdAndOrderStatus(Integer customerId, Integer orderStatus);
    
    List<OrderMain> findByOrderStatus(Integer orderStatus);
    
    @Query("SELECT o FROM OrderMain o WHERE o.customerId = :customerId AND o.createTime BETWEEN :startTime AND :endTime")
    List<OrderMain> findByCustomerIdAndCreateTimeBetween(@Param("customerId") Integer customerId, 
                                                         @Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT o FROM OrderMain o WHERE o.orderStatus IN :statuses")
    List<OrderMain> findByOrderStatusIn(@Param("statuses") List<Integer> statuses);
}