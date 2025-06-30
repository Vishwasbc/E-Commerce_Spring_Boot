package com.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long id;
	@ToString.Exclude
	@Column(length = 20,name = "role_name")
	@Enumerated(EnumType.STRING)//to persist tha data as a string and not as an Integer/Ordinal
	private AppRole roleName;
	public Role(AppRole roleName) {
		super();
		this.roleName = roleName;
	}
	
}
