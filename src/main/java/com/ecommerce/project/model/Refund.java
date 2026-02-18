package com.ecommerce.project.model;

import java.time.LocalDateTime;

import com.ecommerce.project.enums.RefundStatus;
import com.ecommerce.project.enums.RefundSource;
import jakarta.persistence.*;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private double amount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    // 🔥 NEW FIELD (Differentiate refund type)
    @Enumerated(EnumType.STRING)
    private RefundSource source;

    private String refundTransactionId;

    private LocalDateTime createdAt;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public RefundSource getSource() {
        return source;
    }

    public void setSource(RefundSource source) {
        this.source = source;
    }

    public String getRefundTransactionId() {
        return refundTransactionId;
    }

    public void setRefundTransactionId(String refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
 // 🔥 JSON HELPER (FOR ADMIN REFUND PAGE)
    public String getUserEmail() {
        return (order != null && order.getUser() != null)
                ? order.getUser().getEmail()
                : null;
    }

}
