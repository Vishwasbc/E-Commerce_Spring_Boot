package com.ecommerce.cart.controller;

import com.ecommerce.cart.payload.CartDTO;
import com.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/carts/users/{userIdentifier}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable String userIdentifier,
                                                    @PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(userIdentifier, productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOs, HttpStatus.OK);
    }

    @GetMapping("/carts/users/{userIdentifier}/cart/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable String userIdentifier,
                                           @PathVariable Long cartId) {
        CartDTO cartDTO = cartService.getCart(userIdentifier, cartId);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/carts/users/{userIdentifier}/product/{productId}/quantity/{delta}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable String userIdentifier,
                                                     @PathVariable Long productId,
                                                     @PathVariable Integer delta) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(userIdentifier, productId, delta);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/carts/users/{userIdentifier}/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable String userIdentifier,
                                                        @PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(userIdentifier, cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}

