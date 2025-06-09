package com.example.ecp_backend.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
}