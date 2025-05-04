package com.ecommerce.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private CategoryRepository categoryRepository;

	private ModelMapper modelMapper;

	@Override
	public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		// Page number starts from Zero
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize,sortByAndOrder);
		Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
		List<Category> allCategories = categoryPage.getContent();
		if (allCategories.isEmpty())
			throw new APIException("No Avaliable Categories");
		List<CategoryDTO> categoryDTOs = allCategories.stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
		return new CategoryResponse(categoryDTOs, categoryPage.getNumber(), categoryPage.getSize(),
				categoryPage.getTotalElements(), categoryPage.getTotalPages(), categoryPage.isLast());
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		Category confirmCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName()).orElse(null);
		if (confirmCategory != null) {
			throw new APIException("Category with Category Name: " + categoryDTO.getCategoryName() + " already exists");
		}
		Category category = modelMapper.map(categoryDTO, Category.class);
		Category savedCategory = categoryRepository.save(category);
		return modelMapper.map(savedCategory, CategoryDTO.class);
	}

	@Override
	public CategoryDTO deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		categoryRepository.delete(category);
		return modelMapper.map(category, CategoryDTO.class);
	}

	@Override
	public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
		Category existingCategory = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		existingCategory.setCategoryName(categoryDTO.getCategoryName());
		Category category = categoryRepository.save(existingCategory);
		return modelMapper.map(category, CategoryDTO.class);
	}

}
