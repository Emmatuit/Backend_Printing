package com.example.demo.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SpecificationDTO;
import com.example.demo.Dto.SpecificationOptionDTO;
import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.Subcategory;

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
			@RequestParam("description") String description, @RequestParam("images") List<MultipartFile> images)
			throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException,
			TooManyRequestsException, UnauthorizedException {

		List<String> uploadedImagePaths = new ArrayList<>();

		for (MultipartFile image : images) {
			String imagePath = imagekitService.uploadFile(image);
			uploadedImagePaths.add(imagePath);
		}

		Category category = new Category();
		category.setName(name);
		category.setDescription(description);
		category.setEncryptedImages(uploadedImagePaths); // ✅ multiple image paths

		Category savedCategory = categoryService.addCategory(category);
		CategoryDto savedCategoryDto = convertToCategoryDto(savedCategory);

		return ResponseEntity.ok(savedCategoryDto);
	}

	// Convert Category entity to CategoryDto
	private CategoryDto convertToCategoryDto(Category category) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		categoryDto.setDescription(category.getDescription());
		categoryDto.setEncryptedImages(category.getEncryptedImages());

		// Ensure subcategories is never null
		categoryDto.setSubcategories(
				category.getSubcategories() != null
						? category.getSubcategories().stream().map(this::convertToSubcategoryDto) // Convert each
																									// subcategory
								.collect(Collectors.toList())
						: new ArrayList<>()); // ✅ Ensures an empty list instead of null

		return categoryDto;
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

	// ✅ Convert Subcategory -> SubcategoryDto (Including Products)
	private SubcategoryDto convertToSubcategoryDto(Subcategory subcategory) {
		List<ProductDto> productDtos = subcategory.getProducts() != null
				? subcategory.getProducts().stream().map(this::convertToProductDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		return new SubcategoryDto(subcategory.getId(), subcategory.getName(), productDtos,
				subcategory.getCategory() != null ? subcategory.getCategory().getId() : null // Prevent null issues
		);
	}

	// Method to delete a category by ID
	@DeleteMapping("{categoryId}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId); // Call delete method in service
		return ResponseEntity.noContent().build(); // Return no content on successful deletion
	}

	// Api to get all the Category http://localhost:8080/api/categories
	@GetMapping("/categories")
	public List<CategoryDto> getAllCategories1() {
		// Fetch all categories and return as a list of CategoryDto
		return categoryService.getAllCategories();
	}

}
