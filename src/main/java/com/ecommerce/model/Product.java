package com.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productId;
	@NotBlank
	@Size(min = 5,message = "Product name should have atleast 5 characters")
	private String productName;
	@NotBlank
	@Size(min = 10,message = "Product description should have atleast 10 characters")
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
