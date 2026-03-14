package com.ecommerce.cart.repository;

import com.ecommerce.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdentifier(String userIdentifier);

    @Query("select c from Cart c where c.userIdentifier = ?1 and c.cartId = ?2")
    Optional<Cart> findCartByUserAndId(String userIdentifier, Long cartId);

    List<Cart> findByUserIdentifierAndCartId(String userIdentifier, Long cartId);
}

