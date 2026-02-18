package com.ecommerce.project.returns;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRepo extends JpaRepository<ReturnRequest, Long> {

    Optional<ReturnRequest> findByOrder(
            com.ecommerce.project.model.Order order
    );
}
