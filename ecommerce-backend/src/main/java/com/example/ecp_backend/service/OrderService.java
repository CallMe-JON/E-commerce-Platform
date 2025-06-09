package com.example.ecp_backend.service;


import com.example.ecp_backend.dto.OrderDTO;
import com.example.ecp_backend.dto.OrderItemDTO;
import com.example.ecp_backend.exception.ResourceNotFoundException;
import com.example.ecp_backend.model.*;
import com.example.ecp_backend.repository.OrderRepository;
import com.example.ecp_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService; // To clear the cart after order

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Transactional
    public OrderDTO createOrder(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<CartItem> cartItems = user.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order with an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING"); // Initial status

        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStock() + ", Requested: " + cartItem.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice()); // Price at the time of order
            order.addOrderItem(orderItem);

            totalAmount += product.getPrice() * cartItem.getQuantity();

            // Decrease product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            // Assuming ProductRepository saves automatically due to JPA's dirty checking,
            // otherwise, you'd need productRepository.save(product) here.
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Clear the user's cart after placing the order
        cartService.clearUserCart(username);

        return convertToDto(savedOrder);
    }

    public List<OrderDTO> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return orderRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have access to this order.");
        }
        return convertToDto(order);
    }


    private OrderDTO convertToDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertOrderItemToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertOrderItemToDto(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(orderItem.getProduct().getId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }
}