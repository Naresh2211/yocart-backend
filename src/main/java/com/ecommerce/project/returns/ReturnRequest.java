package com.ecommerce.project.returns;

import java.time.LocalDateTime;

import com.ecommerce.project.model.Order;

import jakarta.persistence.*;

@Entity
@Table(
    name = "return_requests",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "order_id") // prevent duplicate return per order
    }
)
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== ORDER =====
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // RETURN or REPLACEMENT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnType type;

    @Column(length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    /* ================= GETTERS & SETTERS ================= */

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ReturnType getType() {
        return type;
    }

    public void setType(ReturnType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    /* =====================================================
       🔥 JSON HELPER GETTERS (FOR FRONTEND)
       ===================================================== */

    public Long getOrderId() {
        return order != null ? order.getId() : null;
    }

    public String getUserEmail() {
        return (order != null && order.getUser() != null)
                ? order.getUser().getEmail()
                : null;
    }
}
