package com.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.payload.CartDTO;
import com.ecommerce.service.CartService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CartController {
	CartService cartService;
	@PostMapping("/carts/products/{productId}/quantity/{quantity}")
	public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
		CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
		return new ResponseEntity<>(cartDTO,HttpStatus.CREATED);
	}
}
