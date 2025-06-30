package com.ecommerce.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	@Size(min = 5, message = "Street name should be atleast 5 Charaters")
	private String street;
	@NotBlank
	@Size(min = 5, message = "Building name should be atleast 5 Charaters")
	private String buildingName;
	@NotBlank
	@Size(min = 4, message = "City name should be atleast 4 Charaters")
	private String city;
	@NotBlank
	@Size(min = 2, message = "State name should be atleast 2 Charaters")
	private String state;
	@NotBlank
	@Size(min = 5, message = "Country name should be atleast 5 Charaters")
	private String country;
	@NotBlank
	@Size(min = 6, message = "Pin-Code name should be atleast 6 Charaters")
	private String pinCode;
	@ToString.Exclude
	@ManyToMany(mappedBy = "addresses")
	private List<User> user = new ArrayList<>();
}
