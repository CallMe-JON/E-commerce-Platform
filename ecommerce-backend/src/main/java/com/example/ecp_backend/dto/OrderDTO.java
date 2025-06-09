package com.example.ecp_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;
    private List<OrderItemDTO> orderItems;
}