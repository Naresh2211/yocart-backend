package com.ecommerce.project.returns;

public enum ReturnStatus {

    // User submitted request
    REQUESTED,

    // Old values still present in DB (DO NOT REMOVE)
    REFUND_PROCESSED,
    REPLACEMENT_DELIVERED,

    // New unified status
    COMPLETED
}
