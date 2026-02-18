package com.ecommerce.project.returns;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.dto.ReturnDTO;
import com.ecommerce.project.enums.OrderStatus;
import com.ecommerce.project.enums.PaymentStatus;
import com.ecommerce.project.enums.RefundSource;
import com.ecommerce.project.enums.RefundStatus;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.Refund;
import com.ecommerce.project.repository.OrderRepo;
import com.ecommerce.project.repository.ProductRepo;
import com.ecommerce.project.repository.RefundRepo;

@Service
public class ReturnService {

    private final ReturnRepo returnRepo;
    private final OrderRepo orderRepo;
    private final RefundRepo refundRepo;
    private final ProductRepo productRepo;

    public ReturnService(
            ReturnRepo returnRepo,
            OrderRepo orderRepo,
            RefundRepo refundRepo,
            ProductRepo productRepo) {

        this.returnRepo = returnRepo;
        this.orderRepo = orderRepo;
        this.refundRepo = refundRepo;
        this.productRepo = productRepo;
    }

    // =====================================================
    // USER REQUEST RETURN / REPLACEMENT
    // =====================================================
    @Transactional
    public void requestReturn(
            Long orderId,
            String userEmail,
            ReturnType type,
            String reason) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        // Order must be DELIVERED
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Only delivered orders allowed");
        }

        if (order.getDeliveredAt() == null) {
            throw new RuntimeException("Delivery date missing");
        }

        long days = Duration
                .between(order.getDeliveredAt(), LocalDateTime.now())
                .toDays();

        if (days > 7) {
            throw new RuntimeException("Return window expired (7 days)");
        }

        if (returnRepo.findByOrder(order).isPresent()) {
            throw new RuntimeException("Return already requested");
        }

        ReturnRequest req = new ReturnRequest();
        req.setOrder(order);
        req.setType(type);
        req.setReason(reason);
        req.setStatus(ReturnStatus.REQUESTED);
        req.setRequestedAt(LocalDateTime.now());

        returnRepo.save(req);

        // IMPORTANT:
        // Order status remains DELIVERED.
        // No modification here.
    }

    // =====================================================
    // ADMIN COMPLETE RETURN / REPLACEMENT
    // =====================================================
    @Transactional
    public void processRequest(Long requestId) {

        ReturnRequest req = returnRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (req.getStatus() != ReturnStatus.REQUESTED) {
            throw new RuntimeException("Invalid state");
        }

        Order order = req.getOrder();

        req.setStatus(ReturnStatus.COMPLETED);
        req.setProcessedAt(LocalDateTime.now());

        // ================= RETURN FLOW =================
        if (req.getType() == ReturnType.RETURN) {

            if (refundRepo.existsByOrder(order)) {
                throw new RuntimeException("Refund already exists for this order");
            }

            // Restore stock
            order.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepo.save(product);
            });

            // Create refund
            Refund refund = new Refund();
            refund.setOrder(order);
            refund.setAmount(order.getTotalAmount());
            refund.setSource(RefundSource.RETURN);
            refund.setStatus(RefundStatus.REFUNDED);
            refund.setRefundTransactionId(UUID.randomUUID().toString());
            refund.setCreatedAt(LocalDateTime.now());

            refundRepo.save(refund);

            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setStatus(OrderStatus.RETURNED);
        }

        // ================= REPLACEMENT FLOW =================
        else {
            order.setStatus(OrderStatus.REPLACEMENT_DELIVERED);
        }

        orderRepo.save(order);
        returnRepo.save(req);
    }

    // =====================================================
    // ADMIN VIEW
    // =====================================================
    public List<ReturnDTO> getAllReturns() {
        return returnRepo.findAll()
                .stream()
                .map(req -> {
                    ReturnDTO dto = new ReturnDTO();
                    dto.setId(req.getId());
                    dto.setOrderId(req.getOrder().getId());
                    dto.setUserEmail(req.getOrder().getUser().getEmail());
                    dto.setType(req.getType().name());
                    dto.setReason(req.getReason());
                    dto.setStatus(req.getStatus().name());
                    dto.setRequestedAt(req.getRequestedAt());
                    dto.setProcessedAt(req.getProcessedAt());
                    return dto;
                })
                .toList();
    }
}
