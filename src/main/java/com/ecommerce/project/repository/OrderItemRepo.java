package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
}
