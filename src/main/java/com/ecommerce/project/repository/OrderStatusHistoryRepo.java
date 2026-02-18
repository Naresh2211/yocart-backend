package com.ecommerce.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderStatusHistory;

public interface OrderStatusHistoryRepo
        extends JpaRepository<OrderStatusHistory, Long> {

    // ✅ THIS METHOD WAS MISSING
    List<OrderStatusHistory> findByOrderOrderByChangedAtAsc(Order order);
}
