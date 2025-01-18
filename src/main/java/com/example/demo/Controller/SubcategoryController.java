package com.example.demo.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.CategorySubcategoryProductDto;
import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Subcategory;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class SubcategoryController {

	@Autowired
	private SubcategoryService subcategoryService;

	@Autowired
	private CategoryService categoryService;

	@PostMapping("/{categoryId}/subcategories/add")
	public ResponseEntity<SubcategoryDto> addSubcategory(@PathVariable Long categoryId,
			@RequestBody SubcategoryDto subcategoryDto) {
		// Call service to add subcategory and convert to DTO
		SubcategoryDto addedSubcategory = subcategoryService.addSubcategory(categoryId, subcategoryDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(addedSubcategory);
	}

	@Transactional
	@DeleteMapping("/categories/{categoryId}/deleteSubcategories/{subcategoryId}")
	public ResponseEntity<String> deleteSubcategory(@PathVariable Long categoryId, @PathVariable Long subcategoryId) {
		try {
			// Check if the subcategory exists first
			Optional<Subcategory> subcategoryOpt = subcategoryService.findById1(subcategoryId);
			if (!subcategoryOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found");
			}

			// Get the subcategory and validate that it belongs to the specified category
			Subcategory subcategory = subcategoryOpt.get();
			if (!subcategory.getCategory().getId().equals(categoryId)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Subcategory does not belong to the specified category");
			}

			// Call the service to delete the subcategory
			subcategoryService.deleteSubcategory(subcategoryId);

			return ResponseEntity.ok("Subcategory deleted successfully");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// API to get all subcategories and products by category ID
	@GetMapping("/{categoryId}/subcategories-products")
	public ResponseEntity<CategorySubcategoryProductDto> getSubcategoriesAndProducts(@PathVariable Long categoryId) {
		CategorySubcategoryProductDto response = categoryService.getSubcategoriesAndProductsByCategoryId(categoryId);
		return ResponseEntity.ok(response);
	}

}
