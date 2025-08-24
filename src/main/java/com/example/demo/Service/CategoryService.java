package com.example.demo.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import io.imagekit.sdk.models.results.Result;

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

		if (categoryRepository.existsByName(category.getName())) {
			throw new IllegalArgumentException("Category with this name already exists");
		}

		List<ImageInfo> uploadedImages = new ArrayList<>();

// ✅ Temporarily receiving image URLs, not ImageInfo objects
		for (String imageUrl : category.getImages().stream().map(ImageInfo::getUrl).toList()) {
			if (imageUrl != null && !imageUrl.isEmpty()) {
				try (InputStream inputStream = new URL(imageUrl).openStream()) {
					byte[] imageBytes = inputStream.readAllBytes();
					MultipartFile multipartFile = createMultipartFileFromBytes(imageBytes, "image.jpg", "image/jpeg");

					Result result = imagekitService.uploadFileWithResult(multipartFile);

					if (result != null) {
						ImageInfo imageInfo = new ImageInfo(result.getUrl(), result.getFileId());
						uploadedImages.add(imageInfo);
					}
				} catch (IOException e) {
					throw new RuntimeException("Failed to process image: " + imageUrl, e);
				}
			}
		}

		category.setImages(uploadedImages);

		return categoryRepository.save(category);
	}

//
//	public Category addCategory(Category category) throws InternalServerException, BadRequestException,
//    UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {
//
//// Check if a category with the same name already exists
//if (categoryRepository.existsByName(category.getName())) {
//    throw new IllegalArgumentException("Category with this name already exists");
//}
//
//List<ImageInfo> uploadedImages = new ArrayList<>();
//
//for (String imageUrl : category.getEncryptedImages()) { // Still receiving URLs temporarily
//    if (imageUrl != null && !imageUrl.isEmpty()) {
//        try (InputStream inputStream = new URL(imageUrl).openStream()) {
//            byte[] imageBytes = inputStream.readAllBytes();
//            MultipartFile multipartFile = createMultipartFileFromBytes(imageBytes, "image.jpg", "image/jpeg");
//
//            // Upload to ImageKit and get result
//            Result result = imagekitService.uploadFileWithResult(multipartFile);
//
//            if (result != null) {
//                ImageInfo imageInfo = new ImageInfo(result.getUrl(), result.getFileId());
//                uploadedImages.add(imageInfo);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to process image: " + imageUrl, e);
//        }
//    }
//}
//
//// Set the new list of ImageInfo objects
//category.setImages(uploadedImages);
//
//// Save to database
//return categoryRepository.save(category);
//}

	// ✅ Convert Category -> CategoryDto (Including Subcategories)
	private CategoryDto convertToCategoryDto(Category category) {
		List<SubcategoryDto> subcategoryDtos = category.getSubcategories() != null
				? category.getSubcategories().stream().map(this::convertToSubcategoryDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		// Extract just the image URLs from ImageInfo list
		List<String> imageUrls = category.getImages() != null
				? category.getImages().stream().map(ImageInfo::getUrl).collect(Collectors.toList())
				: Collections.emptyList();

		return new CategoryDto(category.getId(), category.getName(), category.getDescription(), imageUrls, // ✅ Only
																											// URLs
																											// passed to
																											// DTO
				subcategoryDtos);
	}

	private ProductDto convertToProductDto(Product product) {
		List<SpecificationDTO> specificationDTOs = product.getSpecifications() != null
				? product.getSpecifications().stream().map(this::convertToSpecificationDto).collect(Collectors.toList())
				: Collections.emptyList();

		return new ProductDto(product.getId(), product.getName(), product.getDescription(),
				// CORRECT: Using BigDecimal directly
				product.getBaseprice(), product.getMinOrderquantity(), product.getMaxQuantity(),
				product.getIncrementStep(), product.getSubcategory() != null ? product.getSubcategory().getId() : null,
				product.getCategory() != null ? product.getCategory().getId() : null, product.getEncryptedImages(),
				specificationDTOs, product.getViews(), product.getCreatedAt());
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
		// 1. Find category
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

		// 2. Delete each image from ImageKit using fileId
		if (category.getImages() != null) {
			for (ImageInfo imageInfo : category.getImages()) {
				if (imageInfo.getFileId() != null && !imageInfo.getFileId().isEmpty()) {
					try {
						imagekitService.deleteFileFromImageKit(imageInfo.getFileId());
					} catch (Exception e) {
						System.err.println("Failed to delete image from ImageKit: " + imageInfo.getFileId());
						e.printStackTrace();
					}
				}
			}
		}

		// 3. Delete category from DB
		categoryRepository.delete(category);
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

		// Fetch only non-deleted products for the sub-category
		List<Product> products = productRepository.findBySubcategoryAndIsDeletedFalse(subcategory);

		// Map products to ProductDto to avoid nesting
		return products.stream().map(this::convertToProductDto).collect(Collectors.toList());
	}

	public List<Product> getProductsBySubcategoryId(Long subcategoryId) {
		return productRepository.findBySubcategoryIdAndIsDeletedFalse(subcategoryId);
	}

	public CategorySubcategoryProductDto getSubcategoriesAndProductsByCategoryId(Long categoryId, Pageable pageable) {
		// 1. Fetch the category
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

		// 2. Fetch all subcategories for the category
		List<Subcategory> subcategories = subcategoryRepository.findByCategoryId(categoryId);

		// 3. For each subcategory, fetch paginated products
		List<SubcategoryDto> subcategoryDtos = subcategories.stream().map(subcategory -> {
			Page<Product> pagedProducts = productRepository.findBySubcategoryIdAndIsDeletedFalse(subcategory.getId(),
					PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
							Sort.by(Sort.Direction.DESC, "createdAt")));

			List<ProductDto> productDtos = pagedProducts.getContent().stream().map(this::convertToProductDto)
					.collect(Collectors.toList());

			return new SubcategoryDto(subcategory.getId(), subcategory.getName(), productDtos,
					subcategory.getCategory().getId());
		}).collect(Collectors.toList());

		return new CategorySubcategoryProductDto(category.getId(), category.getName(), subcategoryDtos);
	}

	// Method to prevent duplicate category names
	public boolean isCategoryNameDuplicate(String categoryName) {
		// Check if a category with the same name already exists
		return categoryRepository.existsByName(categoryName);
	}

	public Category editCategory(Long categoryId, String name, String description, List<MultipartFile> newImages,
			List<String> fileIdsToReplace) throws Exception {

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Category not found"));

		// Update name if provided
		if (name != null && !name.isBlank()) {
			category.setName(name);
		}

		// Update description if provided
		if (description != null && !description.isBlank()) {
			category.setDescription(description);
		}

		// Replace images if needed
		if (newImages != null && fileIdsToReplace != null && newImages.size() == fileIdsToReplace.size()) {
			for (int i = 0; i < fileIdsToReplace.size(); i++) {
				String oldFileId = fileIdsToReplace.get(i);
				MultipartFile newImage = newImages.get(i);

				// Delete from ImageKit
				imagekitService.deleteFileFromImageKit(oldFileId);

				// Upload new image
				Result result = imagekitService.uploadFileWithResult(newImage);
				ImageInfo newImageInfo = new ImageInfo(result.getUrl(), result.getFileId());

				// Replace in DB image list
				List<ImageInfo> imageList = category.getImages();
				for (int j = 0; j < imageList.size(); j++) {
					if (imageList.get(j).getFileId().equals(oldFileId)) {
						imageList.set(j, newImageInfo);
						break;
					}
				}
			}
		}

		return categoryRepository.save(category);
	}

}
