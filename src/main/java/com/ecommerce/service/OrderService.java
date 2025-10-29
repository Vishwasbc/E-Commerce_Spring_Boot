package com.ecommerce.service;

import com.ecommerce.payload.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(
            Long addressId,
            String paymentMethod,
            String pgName,
            String pgPaymentId,
            String pgStatus,
            String pgResponseMessage);
}
