package com.example.ecp_backend.dto;


import com.example.ecp_backend.model.Product;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long id; // CartItem ID
    private int quantity;
    private Long productId;
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private Product product; // Included for Angular frontend to easily access product details
}