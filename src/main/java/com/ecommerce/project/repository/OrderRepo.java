package com.ecommerce.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.User;

public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    Page<Order> findAll(Pageable pageable);

    // 🔥 NEW – paginated user orders
    Page<Order> findByUser(User user, Pageable pageable);
}

