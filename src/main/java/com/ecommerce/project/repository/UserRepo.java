package com.ecommerce.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.project.model.User;
import java.util.Optional;



public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrName(String email, String name);

}
