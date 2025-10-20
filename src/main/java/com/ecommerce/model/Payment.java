package com.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(min = 4, message = "Payment method must be at least 4 characters long")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

    private Payment(Long paymentId ,String pgPaymentId, String pgResponseMessage , String pgName){
        this.paymentId = paymentId;
        this.pgPaymentId = pgPaymentId;
        this.pgResponseMessage = pgResponseMessage;
        this.pgName = pgName;
    }
}