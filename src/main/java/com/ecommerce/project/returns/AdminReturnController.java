package com.ecommerce.project.returns;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.ReturnDTO;

@RestController
@RequestMapping("/api/admin/returns")
public class AdminReturnController {

    private final ReturnService returnService;

    public AdminReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    // ================= GET ALL RETURN / REPLACEMENT REQUESTS =================
    @GetMapping
    public ResponseEntity<List<ReturnDTO>> getAllRequests() {
        return ResponseEntity.ok(returnService.getAllReturns());
    }

    // ================= PROCESS REQUEST (ADMIN ACTION) =================
    @PutMapping("/{requestId}/process")
    public ResponseEntity<String> processRequest(
            @PathVariable Long requestId) {

        returnService.processRequest(requestId);

        return ResponseEntity.ok("Return / Replacement processed successfully");
    }
}
