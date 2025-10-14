package com.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long addressId;
	@NotBlank
	@Size(min = 5, message = "Street name should be at least 5 Characters")
	private String street;
	@NotBlank
	@Size(min = 5, message = "Building name should be at least 5 Characters")
	private String buildingName;
	@NotBlank
	@Size(min = 4, message = "City name should be at least 4 Characters")
	private String city;
	@NotBlank
	@Size(min = 2, message = "State name should be at least 2 Characters")
	private String state;
	@NotBlank
	@Size(min = 5, message = "Country name should be at least 5 Characters")
	private String country;
	@NotBlank
	@Size(min = 6, message = "Pin-Code name should be at least 6 Characters")
	private String pinCode;
	@ToString.Exclude
	@ManyToMany(mappedBy = "addresses")
	private List<User> user = new ArrayList<>();
}
