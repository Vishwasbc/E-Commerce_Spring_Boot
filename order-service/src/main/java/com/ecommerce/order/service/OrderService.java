package com.ecommerce.order.service;

import com.ecommerce.order.payload.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(
            String email,
            Long addressId,
            String paymentMethod,
            String pgName,
            String pgPaymentId,
            String pgStatus,
            String pgResponseMessage);
}

