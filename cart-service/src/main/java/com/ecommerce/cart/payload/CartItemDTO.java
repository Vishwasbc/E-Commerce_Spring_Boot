package com.ecommerce.cart.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}

