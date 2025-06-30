package com.ecommerce.security.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
	@NotBlank
	@Size(min = 3, max = 20)
	private String username;
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	@NotBlank
	@Size(min = 8, max = 20)
	private String password;
	private Set<String> role;
}
