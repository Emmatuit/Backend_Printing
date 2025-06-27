package com.example.demo.Controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.demo.Dto.CategorySubcategoryProductDto;
import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Subcategory;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class SubcategoryController {

	@Autowired
	private SubcategoryService subcategoryService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubcategoryRepository subcategoryRepository;

	@PostMapping("/{categoryId}/subcategories/add")
	public ResponseEntity<SubcategoryDto> addSubcategory(@PathVariable("categoryId") Long categoryId,
			@RequestBody SubcategoryDto subcategoryDto) {
		// Call service to add subcategory and convert to DTO
		SubcategoryDto addedSubcategory = subcategoryService.addSubcategory(categoryId, subcategoryDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(addedSubcategory);
	}

//	@Transactional
//	@DeleteMapping("/{categoryId}/deleteSubcategories/{subcategoryId}")
//	public ResponseEntity<String> deleteSubcategory(@PathVariable("categoryId") Long categoryId, @PathVariable("subcategoryId") Long subcategoryId) {
//		try {
//			// Check if the subcategory exists first
//			Optional<Subcategory> subcategoryOpt = subcategoryService.findById1(subcategoryId);
//			if (!subcategoryOpt.isPresent()) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found");
//			}
//
//			// Get the subcategory and validate that it belongs to the specified category
//			Subcategory subcategory = subcategoryOpt.get();
//			if (!subcategory.getCategory().getId().equals(categoryId)) {
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//						.body("Subcategory does not belong to the specified category");
//			}
//
//			// Call the service to delete the subcategory
//			subcategoryService.deleteSubcategory(subcategoryId);
//
//			return ResponseEntity.ok("Subcategory deleted successfully");
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//		}
//	}


	// API to get all subcategories and products by category ID
	@GetMapping("/{categoryId}/subcategories-products")
	public ResponseEntity<CategorySubcategoryProductDto> getSubcategoriesAndProducts(
	    @PathVariable("categoryId") Long categoryId,
	    @RequestParam(name = "page", defaultValue = "0") int page,
	    @RequestParam(name = "size", defaultValue = "10") int size) {

	    CategorySubcategoryProductDto response = categoryService.getSubcategoriesAndProductsByCategoryId(categoryId, page, size);
	    return ResponseEntity.ok(response);
	}


	@Transactional
	@DeleteMapping("/subcategories/{id}")
	public ResponseEntity<String> deleteSubcategory(@PathVariable("id") Long id) {
	    Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);

	    if (optionalSubcategory.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found");
	    }

	    Subcategory subcategory = optionalSubcategory.get();

	    // 🚨 Delete all products associated with this subcategory
	    for (Product product : subcategory.getProducts()) {
	        productRepository.delete(product);
	    }

	    // Optional: clear the list (safe cleanup)
	    subcategory.getProducts().clear();
	    subcategoryRepository.save(subcategory);

	    // 🧼 Remove from category too
	    if (subcategory.getCategory() != null) {
	        Category category = subcategory.getCategory();
	        category.getSubcategories().remove(subcategory);
	        subcategory.setCategory(null);
	    }

	    // 💣 Delete the subcategory
	    subcategoryRepository.delete(subcategory);

	    return ResponseEntity.ok("Subcategory and all related products deleted successfully");
	}

	@PutMapping("/subcategories/{id}")
    public ResponseEntity<?> updateSubcategoryName(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
        String newName = request.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Subcategory name cannot be empty");
        }

        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
        if (optionalSubcategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found");
        }

        // Optional: check if name already exists (to avoid duplicates)
        Optional<Subcategory> existing = subcategoryRepository.findByName(newName.trim());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Subcategory name already exists");
        }

        Subcategory subcategory = optionalSubcategory.get();
        subcategory.setName(newName.trim());
        subcategoryRepository.save(subcategory);

        return ResponseEntity.ok("Subcategory name updated successfully");
    }







	@GetMapping("/{categoryId}/subcategories/exists")
	public ResponseEntity<String> checkSubcategoryExists(
	        @PathVariable("categoryId") Long categoryId,
	        @RequestParam("name") String name) {

	    boolean exists = subcategoryRepository.existsByNameIgnoreCaseAndCategoryId(name.trim(), categoryId);

	    if (exists) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Subcategory already exists in this category");
	    } else {
	        return ResponseEntity.ok("Subcategory does not exist in this category");
	    }
	}

	@GetMapping("/categories/{categoryId}/subcategories/id")
	public ResponseEntity<?> getSubcategoryIdByName(
	        @PathVariable("categoryId") Long categoryId,
	        @RequestParam("name") String subcategoryName) {

	    Optional<Subcategory> subcategoryOpt = subcategoryRepository.findByCategoryIdAndName(categoryId, subcategoryName.trim());

	    if (subcategoryOpt.isPresent()) {
	        return ResponseEntity.ok(subcategoryOpt.get().getId());
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found");
	    }
	}






}
