package com.ecommerce.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.config.AppConstants;
import com.ecommerce.payload.ProductDTO;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductController {

	ProductService productService;

	@PostMapping("/admin/categories/{categoryId}/product")
	public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
		return new ResponseEntity<>(productService.addProduct(productDTO, categoryId), HttpStatus.CREATED);
	}

	@GetMapping("/public/products")
	public ResponseEntity<ProductResponse> getAllProducts(
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
			@RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
			@RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
		return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
	}

	@GetMapping("/public/categories/{categoryId}/products")
	public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
			@RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
			@RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
		return new ResponseEntity<>(productService.searchByCategory(categoryId,pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
	}

	@GetMapping("/public/products/keyword/{keyword}")
	public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
			@RequestParam(defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
			@RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
		return new ResponseEntity<>(productService.searchByKeyword(keyword,pageNumber, pageSize, sortBy, sortOrder), HttpStatus.FOUND);
	}

	@PutMapping("/admin/product/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Long productId) {
		return new ResponseEntity<>(productService.updateProduct(productDTO, productId), HttpStatus.OK);
	}
	
	@DeleteMapping("/admin/product/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
		return new ResponseEntity<>(productService.deleteProduct(productId),HttpStatus.OK);
	}
	
	@PutMapping("/products/{productId}/image")
	public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,@RequestParam("Image") MultipartFile image) throws IOException{
		return new ResponseEntity<>(productService.updateProductImage(productId,image),HttpStatus.OK);
	}
}
