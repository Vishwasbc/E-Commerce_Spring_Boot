package com.ecommerce.order.service.impl;

import com.ecommerce.common.exceptions.APIException;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.Payment;
import com.ecommerce.order.payload.OrderDTO;
import com.ecommerce.order.payload.OrderItemDTO;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.PaymentRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        // In a full implementation, you'd load cart contents from cart-service and address from a user/address service.
        // Here we create a minimal order shell.

        if (email == null || email.isBlank()) {
            throw new APIException("Email is required to place an order");
        }

        Order order = new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0);
        order.setStatus("Order Accepted !");
        order.setAddressId(addressId);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // No real items yet – this is a scaffold.
        List<OrderItem> orderItems = new ArrayList<>();

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setOrderItems(new ArrayList<>());
        for (OrderItem item : orderItems) {
            orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class));
        }
        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}

