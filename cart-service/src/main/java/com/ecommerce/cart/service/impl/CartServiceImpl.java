package com.ecommerce.cart.service.impl;

import com.ecommerce.cart.model.Cart;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.payload.CartDTO;
import com.ecommerce.cart.payload.CartItemDTO;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.common.exceptions.APIException;
import com.ecommerce.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
//    private final ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(String userIdentifier, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userIdentifier);

        CartItem existing = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if (existing != null) {
            throw new APIException("Product already exists in the cart");
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(productId);
        item.setQuantity(quantity);
        // Price / discount should ideally come from catalog-service; for now caller must send it, or it's zero.
        item.setDiscount(0.0);
        item.setProductPrice(0.0);

        cartItemRepository.save(item);
        cart.getCartItems().add(item);

        return mapToDto(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No cart exists");
        }
        return carts.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCart(String userIdentifier, Long cartId) {
        Cart cart = cartRepository.findCartByUserAndId(userIdentifier, cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        return mapToDto(cart);
    }

    @Override
    public CartDTO updateProductQuantityInCart(String userIdentifier, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserIdentifier(userIdentifier)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userIdentifier", userIdentifier));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if (cartItem == null) {
            throw new APIException("Product not available in the cart");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new APIException("Resulting quantity cannot be negative");
        }
        if (newQuantity == 0) {
            cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cart.getCartId());
        } else {
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }

        return mapToDto(cart);
    }

    @Override
    public String deleteProductFromCart(String userIdentifier, Long cartId, Long productId) {
        Cart cart = cartRepository.findCartByUserAndId(userIdentifier, cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cartItemRepository.deleteCartItemByProductIdAndCartId(productId, cart.getCartId());
        return "Product removed from the cart";
    }

    private Cart getOrCreateCart(String userIdentifier) {
        return cartRepository.findByUserIdentifier(userIdentifier)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .userIdentifier(userIdentifier)
                            .totalPrice(0.0)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    private CartDTO mapToDto(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setTotalPrice(cart.getTotalPrice());
        List<CartItemDTO> items = cart.getCartItems().stream().map(item -> {
            CartItemDTO itemDto = new CartItemDTO();
            itemDto.setCartItemId(item.getCartItemId());
            itemDto.setProductId(item.getProductId());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setDiscount(item.getDiscount());
            itemDto.setProductPrice(item.getProductPrice());
            return itemDto;
        }).collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }
}

