package com.ecommerce.service.impl;

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
import com.ecommerce.service.CartService;
import com.ecommerce.utility.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

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
        if (quantity == null || quantity <= 0) {
            throw new APIException("Quantity must be a positive integer");
        }

        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Check duplicate in cart
        CartItem existingItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (existingItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        // Stock validation
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        // Decrement stock and persist
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // Create cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        try {
            cartItemRepository.save(newCartItem);
        } catch (DataIntegrityViolationException ex) {
            throw new APIException("Product " + product.getProductName() + " could not be added to cart due to concurrent modification");
        }

        // Ensure in-memory cart reflects the new item for DTO mapping
        cart.getCartItems().add(newCartItem);

        // Recalculate cart total deterministically using BigDecimal
        BigDecimal total = cart.getCartItems().stream()
                .map(ci -> BigDecimal.valueOf(ci.getProductPrice()).multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total.doubleValue());

        Cart savedCart = cartRepository.save(cart);

        // Map to DTO without mutating domain entities
        CartDTO cartDTO = modelMapper.map(savedCart, CartDTO.class);
        List<ProductDTO> products = savedCart.getCartItems().stream()
                .map(item -> {
                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    dto.setQuantity(item.getQuantity()); // ordered quantity
                    return dto;
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No Carts Exist");
        }
        return carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems().stream()
                            .map(ci -> {
                                ProductDTO productDTO = modelMapper.map(ci.getProduct(), ProductDTO.class);
                                productDTO.setQuantity(ci.getQuantity());
                                return productDTO;
                            })
                            .collect(Collectors.toList());
                    cartDTO.setProducts(products);
                    return cartDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        // Map cart to DTO and build product DTOs using CartItem.quantity without mutating Product entity
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(ci -> {
                    ProductDTO dto = modelMapper.map(ci.getProduct(), ProductDTO.class);
                    dto.setQuantity(ci.getQuantity()); // ordered quantity
                    return dto;
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }
}
