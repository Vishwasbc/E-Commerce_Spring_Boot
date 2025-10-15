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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createOrGetCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        validateProductAvailability(product, quantity);

        CartItem existingItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (existingItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        updateCartTotalPrice(cart);
        cartRepository.save(cart);

        return mapCartToDTO(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No cart exists");
        }

        return carts.stream()
                .map(this::mapCartToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        return mapCartToDTO(cart);
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId).orElse(null);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        validateProductAvailability(product, quantity);

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart");
        }

        int newQuantity = cartItem.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0) {
            return removeProductAndUpdateCart(cart, cartItem);
        }

        cartItem.setQuantity(newQuantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount());
        cartItemRepository.save(cartItem);

        updateCartTotalPrice(cart);
        cartRepository.save(cart);

        return mapCartToDTO(cart);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        updateCartTotalPrice(cart);
        cartRepository.save(cart);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart!";
    }

    // ------------------ Helper Methods ------------------

    private Cart createOrGetCart() {
        return cartRepository.findCartByEmail(authUtil.loggedInEmail())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setTotalPrice(0.0);
                    newCart.setUser(authUtil.loggedInUser());
                    return cartRepository.save(newCart);
                });
    }

    private void validateProductAvailability(Product product, int requestedQuantity) {
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }
        if (product.getQuantity() < requestedQuantity) {
            throw new APIException("Please order " + product.getProductName() +
                    " less than or equal to available quantity: " + product.getQuantity());
        }
    }

    private void updateCartTotalPrice(Cart cart) {
        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(totalPrice);
    }

    private CartDTO mapCartToDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }

    private CartDTO removeProductAndUpdateCart(Cart cart, CartItem cartItem) {
        cartItemRepository.deleteById(cartItem.getCartItemId());
        updateCartTotalPrice(cart);
        cartRepository.save(cart);
        return mapCartToDTO(cart);
    }
}
