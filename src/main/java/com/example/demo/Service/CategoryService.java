package com.example.demo.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.CategoryDto;
import com.example.demo.Dto.CategorySubcategoryProductDto;
import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SubcategoryDto;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
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

	public Category addCategory(Category category)
	        throws InternalServerException, BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {
	    // Check if a category with the same name already exists
	    if (categoryRepository.existsByName(category.getName())) {
	        throw new IllegalArgumentException("Category with this name already exists");
	    }

	    // If the category has an image URL, fetch it and convert it into a MultipartFile
	    if (category.getEncryptedImage() != null && !category.getEncryptedImage().isEmpty()) {
	        try {
	            URL url = new URL(category.getEncryptedImage());
	            InputStream inputStream = url.openStream();

	            // Convert InputStream to byte array
	            byte[] imageBytes = inputStream.readAllBytes(); // Java 9+
	            inputStream.close();

	            // Create a MultipartFile using a helper method
	            MultipartFile multipartFile = createMultipartFileFromBytes(imageBytes, "image.jpg", "image/jpeg");

	            // Upload the file to ImageKit
	            String fullUrl = imagekitService.uploadFile(multipartFile);
	            category.setEncryptedImage(fullUrl); // Save the full URL

	        } catch (IOException e) {
	            throw new RuntimeException("Failed to fetch image from URL and upload", e);
	        }
	    }

	    // Save the category in the database
	    return categoryRepository.save(category);
	}

	// Helper method to create a MultipartFile from bytes
	private MultipartFile createMultipartFileFromBytes(byte[] bytes, String fileName, String contentType) {
	    return new MultipartFile() {
	        @Override
	        public String getName() {
	            return "file";
	        }

	        @Override
	        public String getOriginalFilename() {
	            return fileName;
	        }

	        @Override
	        public String getContentType() {
	            return contentType;
	        }

	        @Override
	        public boolean isEmpty() {
	            return bytes.length == 0;
	        }

	        @Override
	        public long getSize() {
	            return bytes.length;
	        }

	        @Override
	        public byte[] getBytes() throws IOException {
	            return bytes;
	        }

	        @Override
	        public InputStream getInputStream() throws IOException {
	            return new ByteArrayInputStream(bytes);
	        }

	        @Override
	        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
	            Files.write(dest.toPath(), bytes);
	        }
	    };
	}


	// Helper method to convert a Category entity to CategoryDto, including
	// subcategories


	// Helper method to convert a Product entity to ProductDto
	private ProductDto convertToProductDto(Product product) {

		// Create and return the ProductDto
		return new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getBaseprice(),
				product.getMinOrderquantity(), product.getMaxQuantity(), product.getIncrementStep(),
				product.getSubcategory().getId(), product.getCategory().getId(), product.getEncryptedImages()

		);
	}


	// Helper method to convert a Subcategory entity to SubcategoryDto, including
	// products
	private SubcategoryDto convertToSubcategoryDto(Subcategory subcategory) {
		// Convert the list of products to ProductDto (if necessary)
		List<ProductDto> productDtos = subcategory.getProducts() != null
				? subcategory.getProducts().stream().map(this::convertToProductDto).collect(Collectors.toList())
				: Collections.emptyList();

		// Create and return the SubcategoryDto
		return new SubcategoryDto(subcategory.getId(), subcategory.getName(), productDtos,
				subcategory.getCategory().getId());
	}

	public void deleteCategory(Long categoryId) {
		// Find the category by ID
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found"));

		// Get the image path from the category entity
		String imagePath = category.getEncryptedImage();

		// Delete the image file from the filesystem
		if (imagePath != null && !imagePath.isEmpty()) {
			deleteImageFile(imagePath);
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

	// Helper method to convert a Specification entity to SpecificationDto

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

	public List<CategoryDto> getAllCategories() {
	    // Fetch all categories from the repository
	    List<Category> categories = categoryRepository.findAll();

	    // Convert each Category entity to CategoryDto and return the list
	    return categories.stream().map(this::convertToCategoryDto).collect(Collectors.toList());
	}

	private CategoryDto convertToCategoryDto(Category category) {
	    List<SubcategoryDto> subcategoryDtos = category.getSubcategories().stream()
	            .map(this::convertToSubcategoryDto)
	            .collect(Collectors.toList());

	    // Use the existing encrypted image URL directly
	    String imageUrl = category.getEncryptedImage();

	    return new CategoryDto(category.getId(), category.getName(), category.getDescription(),
	                           imageUrl, subcategoryDtos);
	}

}
