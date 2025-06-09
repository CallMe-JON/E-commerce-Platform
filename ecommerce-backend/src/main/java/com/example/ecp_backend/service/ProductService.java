package com.example.ecp_backend.service;


import com.example.ecp_backend.exception.ResourceNotFoundException;
import com.example.ecp_backend.model.Product;
import com.example.ecp_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product , Long id){
        Product exictingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        exictingProduct.setName(product.getName());
        exictingProduct.setPrice(product.getPrice());
        exictingProduct.setImageUrl(product.getImageUrl());
        exictingProduct.setDescription(product.getDescription());
        exictingProduct.setStock(product.getStock());

        return productRepository.save(exictingProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }


}