package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ecommerce.project.dto.CheckoutRequestDTO;
import com.ecommerce.project.dto.OrderDTO;
import com.ecommerce.project.mapper.DTOMapper;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ================= CHECKOUT SELECTED CART ITEMS =================
    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(
            @RequestBody CheckoutRequestDTO request,
            Authentication authentication) {

        Order order = orderService.placeOrder(
                authentication.getName(),
                request.getCartItemIds()
        );

        return ResponseEntity.ok(
                DTOMapper.toOrderDTO(order, null, null) // 🔥 UPDATED
        );
    }

    // ================= GET USER ORDERS =================
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(
            Authentication authentication) {

        List<OrderDTO> orders =
                orderService.getOrdersByUser(authentication.getName());

        return ResponseEntity.ok(orders);
    }
    
    
 // ================= GET USER ORDERS (PAGINATED) =================
    @GetMapping("/paged")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUserPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        Page<OrderDTO> orders =
                orderService.getOrdersByUserPaged(
                        authentication.getName(),
                        pageable
                );

        return ResponseEntity.ok(orders);
    }


    // ================= CANCEL ORDER (USER) =================
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        Order cancelledOrder =
                orderService.cancelOrder(
                        orderId,
                        authentication.getName(),
                        false
                );

        return ResponseEntity.ok(
                DTOMapper.toOrderDTO(cancelledOrder, null, null) // 🔥 UPDATED
        );
    }
}
