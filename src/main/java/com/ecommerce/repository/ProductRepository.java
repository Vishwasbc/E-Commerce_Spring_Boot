package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable page);

    Page<Product> findByProductNameLikeIgnoreCase(String string, Pageable page);

    Optional<Product> findByProductName(String productName);

}
