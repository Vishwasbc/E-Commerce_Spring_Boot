package com.ecommerce.service;

import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.payload.CartDTO;
import com.ecommerce.payload.ProductDTO;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.util.AuthUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
	CartRepository cartRepository;
	AuthUtil authUtil;
	ProductRepository productRepository;
	CartItemRepository cartItemRepository;
	ModelMapper modelMapper;

	private Cart createCart() {
		Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
		if (userCart != null) {
			return userCart;
		} else {
			Cart cart = new Cart();
			cart.setTotalPrice(0.0);
			cart.setUser(authUtil.loggedInUser());
			return cartRepository.save(cart);
		}
	}

	@Override
	public CartDTO addProductToCart(Long productId, Integer quantity) {
		Cart cart = createCart();

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

		if (cartItem != null) {
			throw new APIException("Product " + product.getProductName() + " already exists in the cart");
		}
		if (product.getQuantity() == 0) {
			throw new APIException(product.getProductName() + " is not available");
		}
		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make an order of the " + product.getProductName()
					+ " less than or equal to the quantity " + product.getQuantity() + ".");
		}

		CartItem newCartItem = new CartItem();
		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setProductPrice(product.getSpecialPrice());

		cart.getCartItems().add(newCartItem);
		cartItemRepository.save(newCartItem);

		product.setQuantity(product.getQuantity() - quantity);
		productRepository.save(product);

		cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
		Cart savedCart = cartRepository.save(cart);

		CartDTO cartDTO = modelMapper.map(savedCart, CartDTO.class);

		List<ProductDTO> products = savedCart.getCartItems().stream().map(item -> {
			ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
			dto.setQuantity(item.getQuantity());
			return dto;
		}).toList();
		cartDTO.setProducts(products);
		return cartDTO;
	}

	@Override
	public List<CartDTO> getAllCarts() {
		List<Cart> carts = cartRepository.findAll();
		if(carts.size()==0) {
			throw new APIException("No Carts Exist");
		}
		
		return Collections.emptyList();
	}

}
