package com.ecommerce.project.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.dto.OrderDTO;
import com.ecommerce.project.enums.OrderStatus;
import com.ecommerce.project.enums.PaymentMethod;
import com.ecommerce.project.enums.PaymentStatus;
import com.ecommerce.project.mapper.DTOMapper;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repository.*;
import com.ecommerce.project.returns.ReturnRepo;
import com.ecommerce.project.returns.ReturnRequest;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final RefundService refundService;
    private final ReturnRepo returnRepo;

    public OrderService(
            OrderRepo orderRepo,
            OrderItemRepo orderItemRepo,
            CartRepo cartRepo,
            CartItemRepo cartItemRepo,
            UserRepo userRepo,
            ProductRepo productRepo,
            RefundService refundService,
            ReturnRepo returnRepo) {

        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.refundService = refundService;
        this.returnRepo = returnRepo;
    }

    // =========================================================
    // USER: PLACE ORDER
    // =========================================================
    @Transactional
    public Order placeOrder(String email, List<Long> cartItemIds) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> selectedItems = cart.getItems().stream()
                .filter(item -> cartItemIds.contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No cart items selected");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = selectedItems.stream().map(cartItem -> {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepo.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(cartItem.getQuantity());
            oi.setPrice(product.getPrice());

            return oi;
        }).toList();

        order.setItems(orderItems);

        double total = orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        order.setTotalAmount(total);

        Order savedOrder = orderRepo.save(order);
        orderItemRepo.saveAll(orderItems);

        for (CartItem item : selectedItems) {
            int remaining = item.getMaxQuantity() - item.getQuantity();

            if (remaining > 0) {
                item.setQuantity(remaining);
                item.setMaxQuantity(remaining);
                cartItemRepo.save(item);
            } else {
                cart.getItems().remove(item);
                cartItemRepo.delete(item);
            }
        }

        cartRepo.save(cart);
        return savedOrder;
    }

    // =========================================================
    // USER: GET ORDERS (WITH REFUND + RETURN STATUS)
    // =========================================================
    public List<OrderDTO> getOrdersByUser(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepo.findByUser(user);

        return orders.stream()
                .map(order -> {

                    Refund refund = refundService
                            .getRefundByOrder(order)
                            .orElse(null);

                    ReturnRequest returnRequest =
                            returnRepo.findByOrder(order)
                                      .orElse(null);

                    OrderDTO dto = DTOMapper.toOrderDTO(
                            order,
                            refund,
                            returnRequest
                    );

                    // 🔥 Remove underscore from enums before sending to frontend
                    if (dto.getStatus() != null) {
                        dto.setStatus(dto.getStatus().replace("_", " "));
                    }

                    if (dto.getReturnReason() != null) {
                        dto.setReturnReason(dto.getReturnReason().replace("_", " "));
                    }

                    if (dto.getReturnStatus() != null) {
                        dto.setReturnStatus(dto.getReturnStatus().replace("_", " "));
                    }

                    return dto;
                })
                .toList();
    }
    
 // =========================================================
 // USER: GET ORDERS PAGINATED (SAFE ADDITION)
 // =========================================================
 public Page<OrderDTO> getOrdersByUserPaged(String email, Pageable pageable) {

     User user = userRepo.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("User not found"));

     return orderRepo.findByUser(user, pageable)
             .map(order -> {

                 Refund refund = refundService
                         .getRefundByOrder(order)
                         .orElse(null);

                 ReturnRequest returnRequest =
                         returnRepo.findByOrder(order)
                                   .orElse(null);

                 OrderDTO dto = DTOMapper.toOrderDTO(
                         order,
                         refund,
                         returnRequest
                 );

                 if (dto.getStatus() != null) {
                     dto.setStatus(dto.getStatus().replace("_", " "));
                 }

                 if (dto.getReturnReason() != null) {
                     dto.setReturnReason(dto.getReturnReason().replace("_", " "));
                 }

                 if (dto.getReturnStatus() != null) {
                     dto.setReturnStatus(dto.getReturnStatus().replace("_", " "));
                 }

                 return dto;
             });
 }


    // =========================================================
    // USER / ADMIN: CANCEL ORDER
    // =========================================================
    @Transactional
    public Order cancelOrder(Long orderId, String email, boolean isAdmin) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!isAdmin && !order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Not allowed");
        }

        if (order.getStatus() == OrderStatus.SHIPPED ||
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel now");
        }

        // 🔥 Restore stock on cancel
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepo.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepo.save(order);
    }

    // =========================================================
    // ADMIN: UPDATE ORDER STATUS
    // =========================================================
    @Transactional
    public Order updateOrderStatusByAdmin(Long orderId, OrderStatus newStatus) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (newStatus == OrderStatus.SHIPPED &&
            currentStatus != OrderStatus.CONFIRMED &&
            currentStatus != OrderStatus.PLACED) {
            throw new RuntimeException("Order cannot be shipped");
        }

        if (newStatus == OrderStatus.DELIVERED) {

            if (currentStatus != OrderStatus.SHIPPED) {
                throw new RuntimeException("Order not shipped yet");
            }

            order.setDeliveredAt(LocalDateTime.now());

            // COD auto paid on delivery
            if (order.getPaymentStatus() == PaymentStatus.PENDING &&
                order.getPaymentMethod() == PaymentMethod.COD) {
                order.setPaymentStatus(PaymentStatus.PAID);
            }
        }

        order.setStatus(newStatus);
        return orderRepo.save(order);
    }

    // =========================================================
    // ADMIN: GET ALL ORDERS
    // =========================================================
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable);
    }
}
