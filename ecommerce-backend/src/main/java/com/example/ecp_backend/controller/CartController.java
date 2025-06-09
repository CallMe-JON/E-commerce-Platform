package com.example.ecp_backend.controller;


import com.example.ecp_backend.dto.CartItemDTO;
import com.example.ecp_backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getUserCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getUserCart(authentication.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<List<CartItemDTO>> addItemToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.addItemToCart(authentication.getName(), productId, quantity));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<List<CartItemDTO>> removeItemFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.removeItemFromCart(authentication.getName(), cartItemId));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<List<CartItemDTO>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int quantity,
            Authentication authentication) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(authentication.getName(), cartItemId, quantity));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.clearUserCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}