package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.project.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
