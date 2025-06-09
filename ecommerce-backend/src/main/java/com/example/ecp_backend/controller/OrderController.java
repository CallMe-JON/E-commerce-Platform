package com.example.ecp_backend.controller;


import com.example.ecp_backend.dto.OrderDTO;
import com.example.ecp_backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(Authentication authentication) {
        OrderDTO order = orderService.createOrder(authentication.getName());
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getUserOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getUserOrders(authentication.getName()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId, Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(authentication.getName(), orderId));
    }
}