package com.ecommerce.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.enums.RefundSource;
import com.ecommerce.project.model.Refund;
import com.ecommerce.project.service.RefundService;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/{orderId}/request")
    public ResponseEntity<Refund> requestRefund(
            @PathVariable Long orderId,
            Authentication authentication) {

        return ResponseEntity.ok(
                refundService.requestRefund(
                        orderId,
                        authentication.getName(),
                        RefundSource.CANCELLATION   // Manual refund only for cancellation
                )
        );
    }
}
