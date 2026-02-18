package com.ecommerce.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.model.*;
import com.ecommerce.project.repository.*;

@Service
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    public CartService(
            CartRepo cartRepo,
            CartItemRepo cartItemRepo,
            ProductRepo productRepo,
            UserRepo userRepo) {

        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    // ================= ADD TO CART WITH QUANTITY =================
    @Transactional
    public CartItem addToCartWithQuantity(String email, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ AUTO-CREATE CART
        Cart cart = cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepo.findByCartAndProduct(cart, product)
                .orElse(null);

        // 🔥 PRODUCT ALREADY EXISTS → INCREMENT
        if (item != null) {

            int newTotalQty = item.getQuantity() + quantity;

            if (newTotalQty > product.getStock()) {
                throw new RuntimeException("Out of stock");
            }

            item.setQuantity(newTotalQty);
            item.setMaxQuantity(item.getMaxQuantity() + quantity);

            return cartItemRepo.save(item);
        }

        // 🔥 FIRST TIME ADD
        if (quantity > product.getStock()) {
            throw new RuntimeException("Out of stock");
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setMaxQuantity(quantity);

        return cartItemRepo.save(newItem);
    }

    // ================= VIEW CART =================
    public List<CartItem> viewCart(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ AUTO-CREATE CART (FIX)
        Cart cart = cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        return cart.getItems();
    }

    // ================= UPDATE QUANTITY (1 → maxQuantity) =================
    @Transactional
    public CartItem updateCartQuantity(
            String email,
            Long cartItemId,
            int newQuantity) {

        if (newQuantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ AUTO-CREATE CART (SAFETY)
        Cart cart = cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized cart access");
        }

        if (newQuantity > item.getMaxQuantity()) {
            throw new RuntimeException(
                    "Quantity cannot exceed originally added amount");
        }

        item.setQuantity(newQuantity);
        return cartItemRepo.save(item);
    }

    // ================= REMOVE ITEM =================
    @Transactional
    public void removeItem(String email, Long cartItemId) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ AUTO-CREATE CART (SAFETY)
        Cart cart = cartRepo.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepo.save(newCart);
                });

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized cart access");
        }

        cart.getItems().remove(item);
        cartItemRepo.delete(item);
    }
}
