package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	@Query("Select ci from CartItem ci where ci.product.id=?1 and ci.cart.id=?2")
	CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);

}
