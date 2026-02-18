package com.ecommerce.project.controller;

import com.ecommerce.project.dto.LoginRequest;
import com.ecommerce.project.dto.LoginResponse;
import com.ecommerce.project.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

        String role = authentication.getAuthorities()
                                    .iterator()
                                    .next()
                                    .getAuthority()
                                    .replace("ROLE_", "");

        String token = jwtUtil.generateToken(
            authentication.getName(),
            role
        );

        return new LoginResponse(token, role);
    }
}
