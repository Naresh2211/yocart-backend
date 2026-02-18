package com.ecommerce.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.dto.ReturnDTO;
import com.ecommerce.project.enums.OrderStatus;
import com.ecommerce.project.enums.PaymentMethod;
import com.ecommerce.project.enums.PaymentStatus;
import com.ecommerce.project.enums.RefundStatus;
import com.ecommerce.project.enums.RefundSource;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.Refund;
import com.ecommerce.project.repository.OrderRepo;
import com.ecommerce.project.repository.RefundRepo;

@Service
public class RefundService {

    private final RefundRepo refundRepo;
    private final OrderRepo orderRepo;

    public RefundService(RefundRepo refundRepo, OrderRepo orderRepo) {
        this.refundRepo = refundRepo;
        this.orderRepo = orderRepo;
    }

    // =====================================================
    // REQUEST REFUND (ONLY FOR CANCELLED + PAID + NON-COD)
    // =====================================================
    @Transactional
    public Refund requestRefund(
            Long orderId,
            String email,
            RefundSource source) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔒 Must belong to user
        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Not allowed to request refund");
        }

        // 🔥 Must be CANCELLED
        if (order.getStatus() != OrderStatus.CANCELLED) {
            throw new RuntimeException("Refund allowed only for cancelled orders");
        }

        // 🔥 COD not eligible
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            throw new RuntimeException("Refund not applicable for COD orders");
        }

        // 🔥 Must be PAID
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Refund allowed only for PAID orders");
        }

        // 🔥 Prevent duplicate refund
        if (refundRepo.findByOrder(order).isPresent()) {
            throw new RuntimeException("Refund already requested");
        }

        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setAmount(order.getTotalAmount());
        refund.setStatus(RefundStatus.REQUESTED);
        refund.setSource(source);
        refund.setCreatedAt(LocalDateTime.now());

        return refundRepo.save(refund);
    }

    // =====================================================
    // ADMIN COMPLETE REFUND
    // =====================================================
    @Transactional
    public Refund completeRefund(Long refundId) {

        Refund refund = refundRepo.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found"));

        if (refund.getStatus() == RefundStatus.REFUNDED) {
            throw new RuntimeException("Refund already completed");
        }

        Order order = refund.getOrder();

        // 🔥 Update refund
        refund.setStatus(RefundStatus.REFUNDED);
        refund.setRefundTransactionId(UUID.randomUUID().toString());

        // 🔥 Update order payment status
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        orderRepo.save(order);

        return refundRepo.save(refund);
    }

    // =====================================================
    // GET ALL REFUND REQUESTS (ADMIN TAB)
    // =====================================================
    public List<Refund> getAllRefundRequests() {
        return refundRepo.findAll()
                .stream()
                .filter(refund ->
                        refund.getSource() == RefundSource.CANCELLATION
                     || refund.getSource() == RefundSource.RETURN
                )
                .toList();
    }


    // =====================================================
    // GET REFUND BY ORDER
    // =====================================================
    public Optional<Refund> getRefundByOrder(Order order) {
        return refundRepo.findByOrder(order);
    }
}
