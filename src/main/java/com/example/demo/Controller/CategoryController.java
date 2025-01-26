package com.example.demo.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.CategoryDto;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Category;

import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ImagekitService imagekitService;

	private final SubcategoryService subcategoryService;

	public CategoryController(SubcategoryService subcategoryService) {
		this.subcategoryService = subcategoryService;
	}

	// Method to add a new category http://localhost:8080/api/add
	@PostMapping(value = "/add", consumes = { "multipart/form-data" })
	public ResponseEntity<CategoryDto> addCategory(@RequestParam("name") String name,
			@RequestParam("description") String description, @RequestParam("image") MultipartFile image)
			throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {

		// Save the image file and get the path
		String imagePath = imagekitService.uploadFile(image);

		// Create a new Category object
		Category category = new Category();
		category.setName(name);
		category.setDescription(description);
		category.setEncryptedImage(imagePath); // Store the file path in the database

		// Save the category in the database
		Category savedCategory = categoryService.addCategory(category);

		// Convert the saved category to CategoryDto to return
		CategoryDto savedCategoryDto = convertToCategoryDto(savedCategory);

		return ResponseEntity.ok(savedCategoryDto);
	}

	// Convert Category entity to CategoryDto
	private CategoryDto convertToCategoryDto(Category category) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		categoryDto.setDescription(category.getDescription());
		categoryDto.setEncryptedImage(category.getEncryptedImage());
		return categoryDto;
	}

	// Method to delete a category by ID
	@DeleteMapping("{categoryId}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId); // Call delete method in service
		return ResponseEntity.noContent().build(); // Return no content on successful deletion
	}

	// Api to get all the Category http://localhost:8080/api/categories
	@GetMapping("/categories")
	public List<CategoryDto> getAllCategories() {
		// Fetch all categories and return as a list of CategoryDto
		return categoryService.getAllCategories();
	}



}
