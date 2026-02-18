package com.ecommerce.project.dto;

import java.time.LocalDateTime;

import com.ecommerce.project.enums.OrderStatus;

public class OrderHistoryDTO {

    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String changedBy;
    private LocalDateTime changedAt;

    // getters & setters

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(OrderStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
