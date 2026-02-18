package com.ecommerce.project.security;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepo;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier)
            throws UsernameNotFoundException {

        // 🔥 EMAIL OR USERNAME LOGIN
        User user = userRepo
                .findByEmailOrName(identifier, identifier)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email or username: " + identifier
                        )
                );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // ⚠️ keep email as principal
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
