package com.ecommerce.project.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.dto.PaymentResponseDTO;
import com.ecommerce.project.enums.OrderStatus;
import com.ecommerce.project.enums.PaymentMethod;
import com.ecommerce.project.enums.PaymentStatus;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.repository.OrderRepo;

@Service
public class PaymentService {

    private final OrderRepo orderRepo;

    public PaymentService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Transactional
    public PaymentResponseDTO processPayment(
            Long orderId,
            String paymentMethod,
            String userEmail) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔐 Only order owner can pay
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not allowed to pay for this order");
        }

        // ❌ Prevent double payment
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Order already paid");
        }

        // Convert String → ENUM safely
        PaymentMethod method =
                PaymentMethod.valueOf(paymentMethod.toUpperCase());

        String transactionId = UUID.randomUUID().toString();

        order.setPaymentMethod(method);
        order.setTransactionId(transactionId);

        if (method == PaymentMethod.COD) {
            order.setPaymentStatus(PaymentStatus.PENDING);
        } else {
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.CONFIRMED);
        }

        orderRepo.save(order);

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setOrderId(order.getId());
        response.setTransactionId(transactionId);
        response.setPaymentMethod(order.getPaymentMethod().name());
        response.setPaymentStatus(order.getPaymentStatus().name());
        response.setOrderStatus(order.getStatus().name());
        response.setMessage("Payment processed successfully");

        return response;
    }
}
