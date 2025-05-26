package com.example.demo.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SpecificationDTO;
import com.example.demo.Dto.SpecificationOptionDTO;
import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.Subcategory;

import jakarta.annotation.PostConstruct;

@Service
public class SubcategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	private final SubcategoryRepository subcategoryRepository;

	public SubcategoryService(SubcategoryRepository subcategoryRepository) {
		this.subcategoryRepository = subcategoryRepository;
	}

	public SubcategoryDto addSubcategory(Long categoryId, SubcategoryDto subcategoryDto) {
		// Fetch category
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found"));

		// Check for duplicate name
		if (subcategoryRepository.existsByNameAndCategoryId(subcategoryDto.getName(), categoryId)) {
			throw new IllegalArgumentException("Subcategory name already exists in this category");
		}

		// Create and save the subcategory
		Subcategory subcategory = new Subcategory();
		subcategory.setName(subcategoryDto.getName());
		subcategory.setCategory(category); // Associate with category

		Subcategory savedSubcategory = subcategoryRepository.save(subcategory);

		// Convert to SubcategoryDto
		return convertToSubcategoryDto(savedSubcategory);
	}

	// ✅ Convert Product -> ProductDto
	private ProductDto convertToProductDto(Product product) {
		List<SpecificationDTO> specificationDTOs = product.getSpecifications() != null
				? product.getSpecifications().stream().map(this::convertToSpecificationDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		return new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getBaseprice(),
				product.getMinOrderquantity(), product.getMaxQuantity(), product.getIncrementStep(),
				product.getSubcategory() != null ? product.getSubcategory().getId() : null, // Prevent null issues
				product.getCategory() != null ? product.getCategory().getId() : null, // Prevent null issues
				product.getEncryptedImages(), specificationDTOs, product.getViews(), // Add views here
				product.getCreatedAt() // Add createdAt here
		);

	}

	// ✅ Convert Specification -> SpecificationDTO
	private SpecificationDTO convertToSpecificationDto(Specification specification) {
		List<SpecificationOptionDTO> optionDTOs = specification.getOptions() != null ? specification.getOptions()
				.stream().map(this::convertToSpecificationOptionDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		return new SpecificationDTO(specification.getId(), specification.getName(), optionDTOs // ✅ Include options
		);
	}

	// ✅ Convert SpecificationOption -> SpecificationOptionDTO
	private SpecificationOptionDTO convertToSpecificationOptionDto(SpecificationOption option) {
		return new SpecificationOptionDTO(option.getId(), option.getName(), option.getPrice(), option.getImage());
	}

	// ✅ Refactored conversion method
	private SubcategoryDto convertToSubcategoryDto(Subcategory subcategory) {
		SubcategoryDto dto = new SubcategoryDto();
		dto.setId(subcategory.getId());
		dto.setName(subcategory.getName());
		dto.setCategoryId(subcategory.getCategory().getId());

		// Ensure products is never null
		dto.setProducts(subcategory.getProducts() != null
				? subcategory.getProducts().stream().map(this::convertToProductDto).collect(Collectors.toList())
				: new ArrayList<>()); // ✅ Returns an empty list if no products

		return dto;
	}

//	public SubcategoryDto addSubcategory(Long categoryId, SubcategoryDto subcategoryDto) {
//		// Fetch category
//		Category category = categoryRepository.findById(categoryId)
//				.orElseThrow(() -> new IllegalArgumentException("Category not found"));
//
//		// Check for duplicate name
//		boolean exists = subcategoryRepository.existsByNameAndCategoryId(subcategoryDto.getName(), categoryId);
//		if (exists) {
//			throw new IllegalArgumentException("Subcategory name already exists in this category");
//		}
//
//		// Create and save the subcategory
//		Subcategory subcategory = new Subcategory();
//		subcategory.setName(subcategoryDto.getName());
//		subcategory.setCategory(category); // Associate with category
//
//		Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
//
//		// Convert to SubcategoryDto
//		SubcategoryDto responseDto = new SubcategoryDto();
//		responseDto.setId(savedSubcategory.getId());
//		responseDto.setName(savedSubcategory.getName());
//		responseDto.setCategoryId(category.getId()); // Just return the categoryId
//
//		return responseDto;
//	}

	// Delete Subcategory method
	public void deleteSubcategory(Long subcategoryId) {
		// Call custom findById method
		Optional<Subcategory> subcategory = findById1(subcategoryId);
		if (subcategory.isPresent()) {
			subcategoryRepository.delete(subcategory.get());
		} else {
			throw new IllegalArgumentException("Subcategory not found");
		}
	}

	// Custom findById method
	public Optional<Subcategory> findById1(Long subcategoryId) {
		return subcategoryRepository.findById(subcategoryId);
	}

	// Method to get a subcategory by ID
	public Subcategory getSubcategoryById(Long subcategoryId) {
		return subcategoryRepository.findById(subcategoryId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Subcategory not found with id: " + subcategoryId)); // Directly use ResponseStatusException
	}

	@PostConstruct
	public void init() {
		if (subcategoryRepository == null) {
			System.out.println("SubcategoryRepository is null!");
		} else {
			System.out.println("SubcategoryRepository injected successfully!");
		}
	}
}
