package com.example.demo.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Category;
import com.example.demo.model.ImageInfo;
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

	@Autowired
	private CategoryRepository categoryRepository;

	private final SubcategoryService subcategoryService;

	public CategoryController(SubcategoryService subcategoryService) {
		this.subcategoryService = subcategoryService;
	}

	@PostMapping(value = "/add", consumes = { "multipart/form-data" })
	public ResponseEntity<CategoryDto> addCategory(
	        @RequestParam("name") String name,
	        @RequestParam("description") String description,
	        @RequestParam("images") List<MultipartFile> images)
	        throws IOException, InternalServerException, BadRequestException,
	        UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {

	    List<ImageInfo> uploadedImages = new ArrayList<>();

	    for (MultipartFile image : images) {
	        ImageInfo imageInfo = imagekitService.uploadFile(image); // ✅ store URL and fileId
	        uploadedImages.add(imageInfo);
	    }

	    Category category = new Category();
	    category.setName(name);
	    category.setDescription(description);
	    category.setImages(uploadedImages); // ✅ store full image info list

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

	    // ✅ Extract only the image URLs from the ImageInfo objects
	    List<String> imageUrls = category.getImages() != null
	            ? category.getImages().stream()
	                      .map(ImageInfo::getUrl)
	                      .collect(Collectors.toList())
	            : new ArrayList<>();

	    categoryDto.setEncryptedImages(imageUrls); // Still using same field name in DTO for simplicity

	    // ✅ Convert subcategories
	    categoryDto.setSubcategories(
	        category.getSubcategories() != null
	            ? category.getSubcategories().stream()
	                      .map(this::convertToSubcategoryDto)
	                      .collect(Collectors.toList())
	            : new ArrayList<>());

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
				product.getCreatedAt()
				// Add createdAt here
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
		return new SpecificationOptionDTO(option.getId(), option.getName(), option.getImage(), option.getPrice());
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

	// DELETE /api/categories/{categoryId}/delete
	@DeleteMapping("/categories/{categoryId}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId) {
	    categoryService.deleteCategory(categoryId);
	    return ResponseEntity.noContent().build(); // HTTP 204
	}

	// Api to get all the Category http://localhost:8080/api/categories
	@GetMapping("/categories")
	public List<CategoryDto> getAllCategories1() {
		// Fetch all categories and return as a list of CategoryDto
		return categoryService.getAllCategories();
	}

	@GetMapping("/categories/exists")
	public ResponseEntity<?> checkCategoryExists(@RequestParam("name") String name) {
	    boolean exists = categoryRepository.existsByNameIgnoreCase(name.trim());

	    if (exists) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Category already exists");
	    } else {
	        return ResponseEntity.ok("Category does not exist. You can proceed.");
	    }
	}

	@GetMapping("/categories/check-full")
	public ResponseEntity<?> checkCategoryFull(@RequestParam("name") String name) {
	    Optional<Category> categoryOpt = categoryRepository.findByNameIgnoreCase(name.trim());

	    if (categoryOpt.isPresent()) {
	        CategoryDto dto = convertToCategoryDto(categoryOpt.get());
	        return ResponseEntity.ok(Map.of("exists", true, "category", dto));
	    } else {
	        return ResponseEntity.ok(Map.of("exists", false, "message", "Category does not exist. You can proceed."));
	    }
	}


	@PutMapping(value = "/categories/{id}/edit", consumes = {"multipart/form-data"})
	public ResponseEntity<CategoryDto> editCategory(
	        @PathVariable("id") Long id,
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "imagesToReplace", required = false) List<MultipartFile> imagesToReplace,
	        @RequestParam(value = "fileIdsToReplace", required = false) List<String> fileIdsToReplace
	) throws Exception {

	    Category updated = categoryService.editCategory(id, name, description, imagesToReplace, fileIdsToReplace);
	    return ResponseEntity.ok(convertToCategoryDto(updated));
	}


	@GetMapping("/categories/id")
	public ResponseEntity<?> getCategoryIdByName(@RequestParam("name") String name) {
	    Optional<Category> categoryOpt = categoryRepository.findByNameIgnoreCase(name.trim());

	    if (categoryOpt.isPresent()) {
	        return ResponseEntity.ok(categoryOpt.get().getId());
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
	    }
	}




}
