package com.ecommerce.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Size(min = 5, message = "Product name should have at least 5 characters")
    private String productName;

    @NotBlank
    @Size(min = 10, message = "Product description should have at least 10 characters")
    private String description;

    private String image;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double price;

    @NotNull
    private Double discount;

    private Double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}

