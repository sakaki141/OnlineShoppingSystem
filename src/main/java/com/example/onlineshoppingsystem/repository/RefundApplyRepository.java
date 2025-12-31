package com.example.onlineshoppingsystem.repository;

import com.example.onlineshoppingsystem.entity.RefundApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundApplyRepository extends JpaRepository<RefundApply, Integer> {
    List<RefundApply> findByOrderId(String orderId);
    
    List<RefundApply> findByCustomerId(Integer customerId);
    
    List<RefundApply> findBySellerId(Integer sellerId);
    
    List<RefundApply> findByAuditStatus(Integer auditStatus);
    
    @Query("SELECT r FROM RefundApply r WHERE r.orderId = :orderId AND r.itemId = :itemId")
    List<RefundApply> findByOrderIdAndItemId(@Param("orderId") String orderId, @Param("itemId") Integer itemId);
}