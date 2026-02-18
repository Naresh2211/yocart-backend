package com.ecommerce.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<?> makePayment(
            @RequestBody PaymentRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                paymentService.processPayment(
                        request.getOrderId(),
                        request.getPaymentMethod(),
                        authentication.getName()
                )
        );
    }
}
