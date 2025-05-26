package com.example.demo.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.CategoryDto;
import com.example.demo.Dto.CategorySubcategoryProductDto;
import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SpecificationDTO;
import com.example.demo.Dto.SpecificationOptionDTO;
import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SubcategoryRepository;
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

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubcategoryRepository subcategoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ImagekitService imagekitService;

	public Category addCategory(Category category) throws InternalServerException, BadRequestException,
			UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {
		// Check if a category with the same name already exists
		if (categoryRepository.existsByName(category.getName())) {
			throw new IllegalArgumentException("Category with this name already exists");
		}

		// MultipartFile
		List<String> uploadedImageUrls = new ArrayList<>();

		for (String imageUrl : category.getEncryptedImages()) {
			if (imageUrl != null && !imageUrl.isEmpty()) {
				try (InputStream inputStream = new URL(imageUrl).openStream()) {
					byte[] imageBytes = inputStream.readAllBytes();
					MultipartFile multipartFile = createMultipartFileFromBytes(imageBytes, "image.jpg", "image/jpeg");
					String uploadedUrl = imagekitService.uploadFile(multipartFile);
					uploadedImageUrls.add(uploadedUrl);
				} catch (IOException e) {
					throw new RuntimeException("Failed to upload image: " + imageUrl, e);
				}
			}
		}

		category.setEncryptedImages(uploadedImageUrls);

		// Save the category in the database
		return categoryRepository.save(category);
	}

	// ✅ Convert Category -> CategoryDto (Including Subcategories)
	private CategoryDto convertToCategoryDto(Category category) {
		List<SubcategoryDto> subcategoryDtos = category.getSubcategories() != null
				? category.getSubcategories().stream().map(this::convertToSubcategoryDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		return new CategoryDto(category.getId(), category.getName(), category.getDescription(),
				category.getEncryptedImages(), // Image URL
				subcategoryDtos);
	}

	private ProductDto convertToProductDto(Product product) {
	    List<SpecificationDTO> specificationDTOs = product.getSpecifications() != null
	            ? product.getSpecifications().stream()
	                    .map(this::convertToSpecificationDto)
	                    .collect(Collectors.toList())
	            : Collections.emptyList();

	    return new ProductDto(
	            product.getId(),
	            product.getName(),
	            product.getDescription(),
	            // CORRECT: Using BigDecimal directly
	            product.getBaseprice(),
	            product.getMinOrderquantity(),
	            product.getMaxQuantity(),
	            product.getIncrementStep(),
	            product.getSubcategory() != null ? product.getSubcategory().getId() : null,
	            product.getCategory() != null ? product.getCategory().getId() : null,
	            product.getEncryptedImages(),
	            specificationDTOs,
	            product.getViews(),
	            product.getCreatedAt()
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

	// Helper method to create a MultipartFile from bytes
	private MultipartFile createMultipartFileFromBytes(byte[] bytes, String fileName, String contentType) {
		return new MultipartFile() {
			@Override
			public byte[] getBytes() throws IOException {
				return bytes;
			}

			@Override
			public String getContentType() {
				return contentType;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return new ByteArrayInputStream(bytes);
			}

			@Override
			public String getName() {
				return "file";
			}

			@Override
			public String getOriginalFilename() {
				return fileName;
			}

			@Override
			public long getSize() {
				return bytes.length;
			}

			@Override
			public boolean isEmpty() {
				return bytes.length == 0;
			}

			@Override
			public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
				Files.write(dest.toPath(), bytes);
			}
		};
	}

	public void deleteCategory(Long categoryId) {
		// Find the category by ID
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found"));

		// Get the image path from the category entity
		List<String> imagePaths = category.getEncryptedImages();
		if (imagePaths != null) {
			for (String imagePath : imagePaths) {
				deleteImageFile(imagePath);
			}
		}

		// Delete the category from the database
		categoryRepository.delete(category);
	}

	private void deleteImageFile(String imagePath) {
		// Create a File object with the given image path
		File file = new File(imagePath);

		// Check if the file exists and delete it
		if (file.exists()) {
			boolean isDeleted = file.delete();
			if (!isDeleted) {
				throw new RuntimeException("Failed to delete the image file");
			}
		} else {
			throw new RuntimeException("Image file not found at the specified path");
		}
	}

	public List<CategoryDto> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		System.out.println("Categories API Response: " + categories);

		if (categories.isEmpty()) {
			return Collections.emptyList(); // ✅ Returns [] instead of null
		}
		return categories.stream().map(this::convertToCategoryDto).collect(Collectors.toList());
	}

	// Helper method to convert a Specification entity to SpecificationDto
	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
	}

	public List<ProductDto> getProductsBySubcategoryAndCategory(Long categoryId, Long subcategoryId) {
		// Check if the category exists
		categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
		// Check if the sub-category exists
		Subcategory subcategory = subcategoryRepository.findById(subcategoryId)
				.orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));
		// Fetch products for the sub-category
		List<Product> products = productRepository.findBySubcategory(subcategory);
		// Map products to ProductDto to avoid nesting
		return products.stream().map(this::convertToProductDto).collect(Collectors.toList());
	}

	public List<Product> getProductsBySubcategoryId(Long subcategoryId) {
		return productRepository.findBySubcategoryId(subcategoryId);
	}

	public CategorySubcategoryProductDto getSubcategoriesAndProductsByCategoryId(Long categoryId) {
		// Fetch the category by ID
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

		// Get the subcategories associated with the category
		List<Subcategory> subcategories = subcategoryRepository.findByCategoryId(categoryId);

		// For each subcategory, get the associated products
		List<SubcategoryDto> subcategoryDtos = subcategories.stream().map(subcategory -> {
			List<Product> products = productRepository.findBySubcategoryId(subcategory.getId());
			List<ProductDto> productDtos = products.stream().map(this::convertToProductDto)
					.collect(Collectors.toList());
			return new SubcategoryDto(subcategory.getId(), subcategory.getName(), productDtos,
					subcategory.getCategory().getId());
		}).collect(Collectors.toList());

		// Return the response DTO
		return new CategorySubcategoryProductDto(category.getId(), category.getName(), subcategoryDtos);
	}

	// Method to prevent duplicate category names
	public boolean isCategoryNameDuplicate(String categoryName) {
		// Check if a category with the same name already exists
		return categoryRepository.existsByName(categoryName);
	}

}
