package com.ecommerce.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.Refund;

public interface RefundRepo extends JpaRepository<Refund, Long> {

    Optional<Refund> findByOrder(Order order);
    boolean existsByOrder(Order order);

}
