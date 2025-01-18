package com.example.demo.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.model.Category;
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
		boolean exists = subcategoryRepository.existsByNameAndCategoryId(subcategoryDto.getName(), categoryId);
		if (exists) {
			throw new IllegalArgumentException("Subcategory name already exists in this category");
		}

		// Create and save the subcategory
		Subcategory subcategory = new Subcategory();
		subcategory.setName(subcategoryDto.getName());
		subcategory.setCategory(category); // Associate with category

		Subcategory savedSubcategory = subcategoryRepository.save(subcategory);

		// Convert to SubcategoryDto
		SubcategoryDto responseDto = new SubcategoryDto();
		responseDto.setId(savedSubcategory.getId());
		responseDto.setName(savedSubcategory.getName());
		responseDto.setCategoryId(category.getId()); // Just return the categoryId

		return responseDto;
	}

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
