package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.project.dto.CartItemDTO;
import com.ecommerce.project.mapper.DTOMapper;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ================= ADD TO CART WITH QUANTITY =================
    @PostMapping("/add/{productId}/quantity/{quantity}")
    public ResponseEntity<CartItemDTO> addToCart(
            @PathVariable Long productId,
            @PathVariable int quantity,
            Authentication authentication) {

        CartItem item = cartService.addToCartWithQuantity(
                authentication.getName(),
                productId,
                quantity
        );

        return ResponseEntity.ok(DTOMapper.toCartItemDTO(item));
    }

    // ================= VIEW CART =================
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> viewCart(
            Authentication authentication) {

        List<CartItem> items =
                cartService.viewCart(authentication.getName());

        return ResponseEntity.ok(
                DTOMapper.toCartItemDTOs(items)
        );
    }

    // ================= UPDATE QUANTITY =================
    @PutMapping("/update/{cartItemId}/quantity/{quantity}")
    public ResponseEntity<CartItemDTO> updateQuantity(
            @PathVariable Long cartItemId,
            @PathVariable int quantity,
            Authentication authentication) {

        CartItem item = cartService.updateCartQuantity(
                authentication.getName(),
                cartItemId,
                quantity
        );

        return ResponseEntity.ok(
                DTOMapper.toCartItemDTO(item)
        );
    }

    // ================= REMOVE ITEM =================
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        cartService.removeItem(authentication.getName(), cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }
}
