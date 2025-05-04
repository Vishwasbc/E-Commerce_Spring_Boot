package com.ecommerce.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.payload.ProductDTO;
import com.ecommerce.payload.ProductResponse;

public interface ProductService {

	public ProductDTO addProduct(ProductDTO productDTO, Long categoryId);

	public ProductResponse getAllProducts(Integer pageNumeber, Integer pageSize, String sortBy, String sortOrder);

	public ProductResponse searchByCategory(Long categoryId, Integer pageNumeber, Integer pageSize, String sortBy,
			String sortOrder);

	public ProductResponse searchByKeyword(String keyword, Integer pageNumeber, Integer pageSize, String sortBy,
			String sortOrder);

	public ProductDTO updateProduct(ProductDTO productDTO, Long productId);

	public String deleteProduct(Long productId);

	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

}
