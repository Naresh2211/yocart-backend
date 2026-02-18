package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.model.Refund;
import com.ecommerce.project.service.RefundService;

@RestController
@RequestMapping("/api/admin/refunds")
public class AdminRefundController {

    private final RefundService refundService;

    public AdminRefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    // =====================================================
    // ONLY CANCELLED ORDER REFUNDS
    // (Return refunds must NOT appear here)
    // =====================================================
    @GetMapping
    public ResponseEntity<List<Refund>> getAllRefunds() {

        return ResponseEntity.ok(
                refundService.getAllRefundRequests()   // ✅ Correct method
        );
    }

    // =====================================================
    // COMPLETE REFUND (ONLY FOR CANCELLED ORDERS)
    // =====================================================
    @PutMapping("/{refundId}/complete")
    public ResponseEntity<Refund> completeRefund(
            @PathVariable Long refundId) {

        return ResponseEntity.ok(
                refundService.completeRefund(refundId)
        );
    }
}
