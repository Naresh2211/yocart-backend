package com.ecommerce.project.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.AdminOrderDTO;
import com.ecommerce.project.enums.OrderStatus;
import com.ecommerce.project.mapper.DTOMapper;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.Refund;
import com.ecommerce.project.repository.RefundRepo;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.returns.ReturnRepo;
import com.ecommerce.project.returns.ReturnRequest;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final RefundRepo refundRepo;
    private final ReturnRepo returnRepo;

    public AdminOrderController(
            OrderService orderService,
            RefundRepo refundRepo,
            ReturnRepo returnRepo) {

        this.orderService = orderService;
        this.refundRepo = refundRepo;
        this.returnRepo = returnRepo;
    }

    // =====================================================
    // GET ALL ORDERS (WITH REFUND + RETURN INFO)
    // =====================================================
    @GetMapping
    public ResponseEntity<Page<AdminOrderDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AdminOrderDTO> dtoPage =
                orderService.getAllOrders(pageable)
                        .map(order -> {

                            Refund refund =
                                    refundRepo.findByOrder(order)
                                              .orElse(null);

                            ReturnRequest returnRequest =
                                    returnRepo.findByOrder(order)
                                              .orElse(null);

                            return DTOMapper.toAdminOrderDTO(
                                    order,
                                    refund,
                                    returnRequest
                            );
                        });

        return ResponseEntity.ok(dtoPage);
    }

    // =====================================================
    // SHIP ORDER
    // =====================================================
    @PutMapping("/{orderId}/ship")
    public ResponseEntity<AdminOrderDTO> shipOrder(
            @PathVariable Long orderId) {

        Order order =
                orderService.updateOrderStatusByAdmin(
                        orderId, OrderStatus.SHIPPED);

        return buildAdminDTOResponse(order);
    }

    // =====================================================
    // DELIVER ORDER
    // =====================================================
    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<AdminOrderDTO> deliverOrder(
            @PathVariable Long orderId) {

        Order order =
                orderService.updateOrderStatusByAdmin(
                        orderId, OrderStatus.DELIVERED);

        return buildAdminDTOResponse(order);
    }

    // =====================================================
    // CANCEL ORDER
    // =====================================================
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<AdminOrderDTO> cancelOrderByAdmin(
            @PathVariable Long orderId) {

        Order order =
                orderService.cancelOrder(orderId, null, true);

        return buildAdminDTOResponse(order);
    }

    // =====================================================
    // COMMON BUILDER METHOD (Avoid duplication)
    // =====================================================
    private ResponseEntity<AdminOrderDTO> buildAdminDTOResponse(Order order) {

        Refund refund =
                refundRepo.findByOrder(order).orElse(null);

        ReturnRequest returnRequest =
                returnRepo.findByOrder(order).orElse(null);

        AdminOrderDTO dto =
                DTOMapper.toAdminOrderDTO(order, refund, returnRequest);

        return ResponseEntity.ok(dto);
    }
}
