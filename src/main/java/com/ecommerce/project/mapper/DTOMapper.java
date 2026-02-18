package com.ecommerce.project.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.project.dto.*;
import com.ecommerce.project.model.*;
import com.ecommerce.project.returns.ReturnRequest;

public class DTOMapper {

    // ================= PRODUCT =================
    public static ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }

    // ================= CART =================
    public static CartItemDTO toCartItemDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setProduct(toProductDTO(item.getProduct()));
        return dto;
    }

    public static List<CartItemDTO> toCartItemDTOs(List<CartItem> items) {
        return items.stream()
                .map(DTOMapper::toCartItemDTO)
                .collect(Collectors.toList());
    }

    // ================= ORDER ITEM =================
    public static OrderItemDTO toOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setProduct(toProductDTO(item.getProduct()));
        return dto;
    }

    // ================= USER ORDER =================
    public static OrderDTO toOrderDTO(
            Order order,
            Refund refund,
            ReturnRequest returnRequest) {

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());

        // PAYMENT
        if (order.getPaymentMethod() != null) {
            dto.setPaymentMethod(order.getPaymentMethod().name());
        }

        if (order.getPaymentStatus() != null) {
            dto.setPaymentStatus(order.getPaymentStatus().name());
        }

        // REFUND
        if (refund != null) {
            dto.setRefundStatus(refund.getStatus().name());
            dto.setRefundTransactionId(refund.getRefundTransactionId());
        }

        // RETURN
        if (returnRequest != null) {
            dto.setReturnStatus(returnRequest.getStatus().name());
            dto.setReturnType(returnRequest.getType().name());
            dto.setReturnReason(returnRequest.getReason());
        }

        dto.setItems(
                order.getItems()
                        .stream()
                        .map(DTOMapper::toOrderItemDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    // ================= ADMIN ORDER =================
    public static AdminOrderDTO toAdminOrderDTO(
            Order order,
            Refund refund,
            ReturnRequest returnRequest) {

        AdminOrderDTO dto = new AdminOrderDTO();

        dto.setOrderId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setOrderStatus(order.getStatus().name());
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setUserEmail(order.getUser().getEmail());

        if (order.getPaymentMethod() != null) {
            dto.setPaymentMethod(order.getPaymentMethod().name());
        }

        if (order.getPaymentStatus() != null) {
            dto.setPaymentStatus(order.getPaymentStatus().name());
        }

        dto.setTransactionId(order.getTransactionId());

        if (refund != null) {
            dto.setRefundStatus(refund.getStatus().name());
        }

        if (returnRequest != null) {
            dto.setReturnStatus(returnRequest.getStatus().name());
            dto.setReturnReason(returnRequest.getReason());

        }

        dto.setItems(
                order.getItems()
                        .stream()
                        .map(DTOMapper::toOrderItemDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    // ================= ORDER HISTORY =================
    public static OrderHistoryDTO toOrderHistoryDTO(OrderStatusHistory history) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setOldStatus(history.getOldStatus());
        dto.setNewStatus(history.getNewStatus());
        dto.setChangedBy(history.getChangedBy());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }
}
