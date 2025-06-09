package com.example.ecp_backend.service;


import com.example.ecp_backend.dto.CartItemDTO;
import com.example.ecp_backend.exception.ResourceNotFoundException;
import com.example.ecp_backend.model.CartItem;
import com.example.ecp_backend.model.Product;
import com.example.ecp_backend.model.User;
import com.example.ecp_backend.repository.CartItemRepository;
import com.example.ecp_backend.repository.ProductRepository;
import com.example.ecp_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<CartItemDTO> getUserCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return user.getCartItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CartItemDTO> addItemToCart(String username, Long productId, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        Optional<CartItem> existingCartItem = user.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            user.getCartItems().add(cartItem); // Add to user's cart items collection
        }

        cartItemRepository.save(cartItem); // Save or update the cart item
        return getUserCart(username);
    }

    @Transactional
    public List<CartItemDTO> removeItemFromCart(String username, Long cartItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not own this cart item.");
        }

        user.getCartItems().remove(cartItem); // Remove from user's cart items collection
        cartItemRepository.delete(cartItem);
        return getUserCart(username);
    }

    @Transactional
    public List<CartItemDTO> updateCartItemQuantity(String username, Long cartItemId, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not own this cart item.");
        }
        if (quantity <= 0) {
            return removeItemFromCart(username, cartItemId); // Remove if quantity is 0 or less
        }
        if (cartItem.getProduct().getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + cartItem.getProduct().getName());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return getUserCart(username);
    }

    @Transactional
    public void clearUserCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        cartItemRepository.deleteAll(user.getCartItems());
        user.getCartItems().clear(); // Clear the collection as well
    }

    // This method handles the transition from local storage cart to server-side cart upon login.
    // The Angular frontend handles local storage, so this method is mostly for
    // demonstration or if you were to send the local cart data from frontend.
    // For this setup, we'll assume the frontend will *fetch* the server cart after login,
    // and if the server cart is empty, the frontend then adds items from its local storage.
    // Thus, this method might not be directly called by the frontend on login with the current Angular setup.
    // However, if the Angular app were modified to *send* local cart items on login, this is where it would be handled.
    // For now, it's a placeholder to indicate where the logic would go.
    @Transactional
    public void transferLocalCartToUserCart(String username) {
        // In a real application, the Angular frontend would send its local cart data to this endpoint after login.
        // For the current setup, the frontend simply clears its local cart after login and fetches from the server.
        // If the server cart is empty, the frontend might then populate it from its local storage if desired.
        // This method conceptually represents that 'merge' or 'transfer'.
        // For simplicity, we'll assume the frontend drives the cart state.
        // You would typically have a specific API endpoint (e.g., POST /api/cart/sync) for this.
        System.out.println("User " + username + " logged in. Frontend should now synchronize its cart with the server.");
    }


    private CartItemDTO convertToDto(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setProductPrice(cartItem.getProduct().getPrice());
        dto.setProductImageUrl(cartItem.getProduct().getImageUrl());
        // You can include full product details if needed, but for cart display,
        // often a subset is enough.
        dto.setProduct(cartItem.getProduct());
        return dto;
    }
}