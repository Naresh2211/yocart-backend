package com.ecommerce.project.returns;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.ReturnDTO;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    // =====================================================
    // USER → REQUEST RETURN / REPLACEMENT
    // =====================================================
    @PostMapping("/request")
    public ResponseEntity<String> requestReturn(
            @RequestParam Long orderId,
            @RequestParam ReturnType type,
            @RequestParam String reason,
            Authentication authentication) {

        returnService.requestReturn(
                orderId,
                authentication.getName(),
                type,
                reason
        );

        return ResponseEntity.ok(
                type == ReturnType.RETURN
                        ? "Return request submitted successfully"
                        : "Replacement request submitted successfully"
        );
    }

    // =====================================================
    // ADMIN → GET ALL RETURN REQUESTS (DTO FIXED)
    // =====================================================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReturnDTO>> getAllReturns() {

        List<ReturnDTO> requests =
                returnService.getAllReturns();

        return ResponseEntity.ok(requests);
    }

    // =====================================================
    // ADMIN → PROCESS RETURN / REPLACEMENT
    // =====================================================
    @PutMapping("/{requestId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> processRequest(
            @PathVariable Long requestId) {

        returnService.processRequest(requestId);

        return ResponseEntity.ok("Request processed successfully");
    }
}
