package com.ecommerce.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private Long addressId;
    private List<OrderItemDTO> orderItems;
    @JsonFormat(pattern = "dd-MMM-yyyy, hh:mm a")
    private LocalDateTime orderDate;
    private PaymentDTO payment;
    private Double totalAmount;
    private String orderStatus;
}
