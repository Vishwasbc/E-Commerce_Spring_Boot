package com.ecommerce.cart.service;

import com.ecommerce.cart.payload.CartDTO;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(String userIdentifier, Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String userIdentifier, Long cartId);

    CartDTO updateProductQuantityInCart(String userIdentifier, Long productId, Integer quantity);

    String deleteProductFromCart(String userIdentifier, Long cartId, Long productId);
}

