package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.AppRole;
import com.ecommerce.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByRoleName(AppRole appRole);

}
