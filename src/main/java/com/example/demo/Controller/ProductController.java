package com.example.demo.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SpecificationDTO;
import com.example.demo.Dto.SpecificationOptionDTO;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.ClickedProductHistoryRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.SpecificationOptionService;
import com.example.demo.Service.SpecificationService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.Subcategory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.imagekit.sdk.exceptions.BadRequestException;
import io.imagekit.sdk.exceptions.ForbiddenException;
import io.imagekit.sdk.exceptions.InternalServerException;
import io.imagekit.sdk.exceptions.TooManyRequestsException;
import io.imagekit.sdk.exceptions.UnauthorizedException;
import io.imagekit.sdk.exceptions.UnknownException;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private SubcategoryService subcategoryService;

	@Autowired
	private SubcategoryRepository subcategoryRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SpecificationService specificationService;

	@Autowired
	private SpecificationOptionService specificationOptionService;

	@Autowired
	private SpecificationRepository specificationRepository;

	@Autowired
	private ImagekitService imagekitService;

	@Autowired
	private ClickedProductHistoryRepository clickedProductHistoryRepository;

//	@PostMapping("/{subcategoryId}/addProduct")
//	public ResponseEntity<ProductDto> addProductToSubcategory1(@PathVariable("subcategoryId") Long subcategoryId,
//			@RequestParam("name") String name, @RequestParam("description") String description,
//			@RequestParam("baseprice") Double baseprice, @RequestParam("minOrderQuantity") Integer minOrderQuantity,
//			@RequestParam("maxQuantity") Integer maxQuantity, @RequestParam("incrementStep") Integer incrementStep,
//			@RequestParam("images") List<MultipartFile> images, // Product images
//			@RequestParam("specifications") String specificationsJson, // Specifications as JSON
//			@RequestParam("specImages") List<MultipartFile> specImages // Specification images
//	) throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException,
//			TooManyRequestsException, UnauthorizedException {
//
//		// Validate if subcategory exists
//		Subcategory subcategory = subcategoryRepository.findById(subcategoryId)
//				.orElseThrow(() -> new RuntimeException("Subcategory not found"));
//
//		// Upload product images to ImageKit and collect URLs
//		List<String> imageUrls = new ArrayList<>();
//		for (MultipartFile image : images) {
//			if (!image.isEmpty()) {
//				String imageUrl = imagekitService.uploadFileToProduct(image); // Call the ImageKit upload method
//				imageUrls.add(imageUrl); // Add the URL to the list
//			}
//		}
//
//		// Create and save the product
//		Product product = new Product();
//		product.setName(name);
//		product.setDescription(description);
//		product.setBaseprice(baseprice);
//		product.setMinOrderquantity(minOrderQuantity);
//		product.setMaxQuantity(maxQuantity);
//		product.setIncrementStep(incrementStep);
//		product.setEncryptedImages(imageUrls); // Store the paths to the images
//		product.setSubcategory(subcategory); // Link product to subcategory
//		product.setCategory(subcategory.getCategory()); // Set the category
//		product = productService.saveProduct(product);
//
//		// Parse and save specifications
//		ObjectMapper mapper = new ObjectMapper();
//		List<SpecificationDTO> specifications = mapper.readValue(specificationsJson,
//				new TypeReference<List<SpecificationDTO>>() {
//				});
//
//		for (SpecificationDTO specDto : specifications) {
//			Specification specification = new Specification();
//			specification.setName(specDto.getName());
//			specification.setProduct(product);
//			specification = specificationService.saveSpecification(specification);
//
//			int optionIndex = 0; // Reset for each specification
//
//			for (SpecificationOptionDTO optionDto : specDto.getOptions()) {
//				SpecificationOption option = new SpecificationOption();
//				option.setName(optionDto.getName());
//				option.setPrice(optionDto.getPrice());
//
//				if (optionIndex < specImages.size()) {
//					MultipartFile specImage = specImages.get(optionIndex);
//					if (specImage != null && !specImage.isEmpty()) {
//						String specImageUrl = imagekitService.uploadSpecificationImageFile(specImage);
//						option.setImage(specImageUrl);
//					}
//				}
//				option.setSpecification(specification);
//				specificationOptionService.saveSpecificationOption(option);
//				optionIndex++; // Increment after saving the option
//			}
//
//		}
//
//		// Create and return ProductDto
//		ProductDto productDto = new ProductDto();
//		productDto.setId(product.getId());
//		productDto.setName(product.getName());
//		productDto.setDescription(product.getDescription());
//		productDto.setBasePrice(product.getBaseprice());
//		productDto.setMinOrderQuantity(product.getMinOrderquantity());
//		productDto.setMaxQuantity(product.getMaxQuantity());
//		productDto.setIncrementStep(product.getIncrementStep());
//		productDto.setEncryptedImages(product.getEncryptedImages());
//		productDto.setSubcategoryId(product.getSubcategory().getId());
//		productDto.setCategoryId(product.getCategory().getId());
//
//		// Fetch the specifications associated with the product
//		List<Specification> specifications1 = specificationService.getSpecificationsByProduct(product);
//		List<SpecificationDTO> specificationDtos = new ArrayList<>();
//		for (Specification specification : specifications1) {
//			SpecificationDTO specDto = new SpecificationDTO();
//			specDto.setId(specification.getId());
//			specDto.setName(specification.getName());
//
//			// Fetch the options for this specification
//			List<SpecificationOption> options = specificationOptionService
//					.getSpecificationOptionsBySpecification(specification);
//			List<SpecificationOptionDTO> optionDtos = new ArrayList<>();
//			for (SpecificationOption option : options) {
//				SpecificationOptionDTO optionDto = new SpecificationOptionDTO();
//				optionDto.setId(option.getId());
//				optionDto.setName(option.getName());
//				optionDto.setPrice(option.getPrice());
//				optionDto.setImage(option.getImage()); // Set the image path if any
//				optionDtos.add(optionDto);
//			}
//			specDto.setOptions(optionDtos);
//			specificationDtos.add(specDto);
//		}
//
//		// Set the specifications in the productDto
//		productDto.setSpecifications(specificationDtos);
//
//		return new ResponseEntity<>(productDto, HttpStatus.CREATED);
//	}
	@PostMapping("/{subcategoryId}/addProduct")
	public ResponseEntity<ProductDto> addProductToSubcategory1(
	        @PathVariable("subcategoryId") Long subcategoryId,
	        @RequestParam("name") String name,
	        @RequestParam("description") String description,
	        @RequestParam("baseprice") String basePriceStr, // Changed from Double to String
	        @RequestParam("minOrderQuantity") Integer minOrderQuantity,
	        @RequestParam("maxQuantity") Integer maxQuantity,
	        @RequestParam("incrementStep") Integer incrementStep,
	        @RequestParam("images") List<MultipartFile> images,
	        @RequestParam("specifications") String specificationsJson,
	        @RequestParam("specImages") List<MultipartFile> specImages
	) throws IOException, InternalServerException, BadRequestException, UnknownException, 
	       ForbiddenException, TooManyRequestsException, UnauthorizedException {

	    // Validate price input
	    BigDecimal basePrice;
	    try {
	        basePrice = new BigDecimal(basePriceStr).setScale(2, RoundingMode.HALF_UP);
	    } catch (NumberFormatException e) {
	        throw new BadRequestException("Invalid price format", e, false, false, specificationsJson, specificationsJson, null);
	    }

	    // Validate if subcategory exists
	    Subcategory subcategory = subcategoryRepository.findById(subcategoryId)
	            .orElseThrow(() -> new RuntimeException("Subcategory not found"));

	    // Upload product images
	    List<String> imageUrls = images.stream()
	            .filter(image -> !image.isEmpty())
	            .map(image -> {
	                try {
	                    return imagekitService.uploadFileToProduct(image);
	                } catch (Exception e) {
	                    throw new RuntimeException("Failed to upload image", e);
	                }
	            })
	            .collect(Collectors.toList());

	    // Create and save the product
	    Product product = new Product();
	    product.setName(name);
	    product.setDescription(description);
	    product.setBaseprice(basePrice); // Using BigDecimal
	    product.setMinOrderquantity(minOrderQuantity);
	    product.setMaxQuantity(maxQuantity);
	    product.setIncrementStep(incrementStep);
	    product.setEncryptedImages(imageUrls);
	    product.setSubcategory(subcategory);
	    product.setCategory(subcategory.getCategory());
	    product = productService.saveProduct(product);

	    // Parse and save specifications
	    ObjectMapper mapper = new ObjectMapper();
	    List<SpecificationDTO> specifications = mapper.readValue(specificationsJson,
	            new TypeReference<List<SpecificationDTO>>() {});

	    for (SpecificationDTO specDto : specifications) {
	        Specification specification = new Specification();
	        specification.setName(specDto.getName());
	        specification.setProduct(product);
	        specification = specificationService.saveSpecification(specification);

	        int optionIndex = 0;
	        for (SpecificationOptionDTO optionDto : specDto.getOptions()) {
	            SpecificationOption option = new SpecificationOption();
	            option.setName(optionDto.getName());
	            
	            // Convert option price to BigDecimal
	            BigDecimal optionPrice;
	            try {
	                optionPrice = new BigDecimal(optionDto.getPrice().toString())
	                    .setScale(2, RoundingMode.HALF_UP);
	            } catch (Exception e) {
	                throw new BadRequestException("Invalid option price format", e, false, false, specificationsJson, specificationsJson, null);
	            }
	            option.setPrice(optionPrice);

	            if (optionIndex < specImages.size()) {
	                MultipartFile specImage = specImages.get(optionIndex);
	                if (specImage != null && !specImage.isEmpty()) {
	                    String specImageUrl = imagekitService.uploadSpecificationImageFile(specImage);
	                    option.setImage(specImageUrl);
	                }
	            }
	            option.setSpecification(specification);
	            specificationOptionService.saveSpecificationOption(option);
	            optionIndex++;
	        }
	    }

	    // Convert to DTO
	    ProductDto productDto = convertToProductDto(product);
	    
	    // Fetch specifications for response
	    List<Specification> productSpecs = specificationService.getSpecificationsByProduct(product);
	    List<SpecificationDTO> specDtos = productSpecs.stream()
	            .map(spec -> {
	                SpecificationDTO dto = new SpecificationDTO();
	                dto.setId(spec.getId());
	                dto.setName(spec.getName());
	                
	                List<SpecificationOptionDTO> optionDtos = specificationOptionService
	                        .getSpecificationOptionsBySpecification(spec)
	                        .stream()
	                        .map(opt -> new SpecificationOptionDTO(
	                            opt.getId(),
	                            opt.getName(),
	                            opt.getPrice(),
	                            opt.getImage()
	                        ))
	                        .collect(Collectors.toList());
	                
	                dto.setOptions(optionDtos);
	                return dto;
	            })
	            .collect(Collectors.toList());
	    
	    productDto.setSpecifications(specDtos);

	    return new ResponseEntity<>(productDto, HttpStatus.CREATED);
	}

	// Helper method
	private ProductDto convertToProductDto(Product product) {
	    ProductDto dto = new ProductDto();
	    dto.setId(product.getId());
	    dto.setName(product.getName());
	    dto.setDescription(product.getDescription());
	    dto.setBasePrice(product.getBaseprice());
	    dto.setMinOrderQuantity(product.getMinOrderquantity());
	    dto.setMaxQuantity(product.getMaxQuantity());
	    dto.setIncrementStep(product.getIncrementStep());
	    dto.setEncryptedImages(product.getEncryptedImages());
	    dto.setSubcategoryId(product.getSubcategory().getId());
	    dto.setCategoryId(product.getCategory().getId());
	    return dto;
	}
	// ================================================//
	@PostMapping("/calculateTotalPrice")
	public ResponseEntity<BigDecimal> calculateTotalPrice(@RequestParam("productId") Long productId,
			@RequestParam("selectedQuantity") Integer selectedQuantity,
			@RequestBody List<Long> selectedSpecificationIds) {
		try {
			// Call the service to calculate the total price
			BigDecimal totalPrice = productService.calculateTotalPrice(productId, selectedQuantity,
					selectedSpecificationIds);

			// Return the calculated total price
			return ResponseEntity.ok(totalPrice);
		} catch (RuntimeException e) {
			// Handle errors and return a bad request with the error message
			return ResponseEntity.badRequest().body(null);
		}
	}

//	@GetMapping("/RetrieveProduct/{productId}")
//	public ResponseEntity<ProductDto> getProductById(@PathVariable("productId") Long productId) {
//		Product product = productService.getProductAndIncrementViews(productId);
//
//		if (product == null) {
//			return ResponseEntity.notFound().build();
//		}
//
//		// Convert to ProductDto for response
//		ProductDto productDto = new ProductDto();
//		productDto.setId(product.getId());
//		productDto.setName(product.getName());
//		productDto.setDescription(product.getDescription());
//		productDto.setBasePrice(product.getBaseprice());
//		productDto.setMinOrderQuantity(product.getMinOrderquantity());
//		productDto.setMaxQuantity(product.getMaxQuantity());
//		productDto.setIncrementStep(product.getIncrementStep());
//		productDto.setEncryptedImages(product.getEncryptedImages());
//		productDto.setSubcategoryId(product.getSubcategory().getId());
//		productDto.setCategoryId(product.getCategory().getId());
//
//		// Fetch specifications using a service or repository that queries the
//		// Specification table
//		List<Specification> specifications = specificationRepository.findByProductId(productId);
//
//		// Map specifications and their options
//		List<SpecificationDTO> specificationDtos = specifications.stream().map(spec -> {
//			SpecificationDTO specDto = new SpecificationDTO();
//			specDto.setId(spec.getId());
//			specDto.setName(spec.getName());
//			specDto.setOptions(spec.getOptions().stream().map(option -> {
//				SpecificationOptionDTO optionDto = new SpecificationOptionDTO();
//				optionDto.setId(option.getId());
//				optionDto.setName(option.getName());
//				optionDto.setPrice(option.getPrice());
//				optionDto.setImage(option.getImage());
//				return optionDto;
//			}).collect(Collectors.toList()));
//			return specDto;
//		}).collect(Collectors.toList());
//
//		productDto.setSpecifications(specificationDtos);
//		return ResponseEntity.ok(productDto);
//	}

	@GetMapping("/RetrieveProduct/{productId}")
	public ResponseEntity<ProductDto> getProductById(@PathVariable("productId") Long productId) {
		try {
			// Call the service method that increments views and returns ProductDto already
			// mapped
			ProductDto productDto = productService.getProductAndIncrementViews(productId);

			// Now fetch specifications separately and set them into productDto
			List<Specification> specifications = specificationRepository.findByProductId(productId);

			List<SpecificationDTO> specificationDtos = specifications.stream().map(spec -> {
				SpecificationDTO specDto = new SpecificationDTO();
				specDto.setId(spec.getId());
				specDto.setName(spec.getName());
				specDto.setOptions(spec.getOptions().stream().map(option -> {
					SpecificationOptionDTO optionDto = new SpecificationOptionDTO();
					optionDto.setId(option.getId());
					optionDto.setName(option.getName());
					optionDto.setPrice(option.getPrice());
					optionDto.setImage(option.getImage());
					return optionDto;
				}).collect(Collectors.toList()));
				return specDto;
			}).collect(Collectors.toList());

			productDto.setSpecifications(specificationDtos);

			return ResponseEntity.ok(productDto);
		} catch (RuntimeException e) {
			// product not found or other runtime exception
			return ResponseEntity.notFound().build();
		}
	}

	// Display product based on sub-category
	@GetMapping("/{categoryId}/subcategories/{subcategoryId}/products")
	public ResponseEntity<?> getProductsBySubcategoryAndCategory(@PathVariable("categoryId") Long categoryId,
			@PathVariable("subcategoryId") Long subcategoryId) {

		try {
			// Fetch products by subcategory and category, returning DTOs
			List<ProductDto> products = categoryService.getProductsBySubcategoryAndCategory(categoryId, subcategoryId);
			return ResponseEntity.ok(products);
		} catch (IllegalArgumentException e) {
			// Handle errors if category or subcategory not found
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}

	@GetMapping("/products/{id}/similar")
	public ResponseEntity<?> getSimilarProducts(@PathVariable("id") Long id,
			@RequestParam(name = "sessionId", required = false) String sessionId,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = " size", defaultValue = "10") int size, // default size 10
			Principal principal) {
		if (principal == null && sessionId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized: Provide sessionId or JWT");
		}

		try {
			Page<ProductDto> similarProducts = productService.getSimilarProducts(id, page, size);
			return ResponseEntity.ok(similarProducts);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchProducts(@RequestParam(name = "name") String name,
			@RequestParam(name = "sessionId", required = false) String sessionId, Principal principal) {
		// Allow either JWT (via Principal) or sessionId for access
		if (principal == null && sessionId == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized: Provide sessionId or JWT");
		}

		// Fetch products by name (no pagination, no sort)
		List<ProductDto> products = productService.searchProductsByName(name);

		return ResponseEntity.ok(products);
	}

	@GetMapping("/trending")
	public ResponseEntity<?> getTrendingProducts(@RequestParam(name = "sessionId", required = false) String sessionId,
			Principal principal) {

		// Check authorization: either Principal (JWT) or sessionId must be present
		if (principal == null && (sessionId == null || sessionId.isEmpty())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized: Provide sessionId or JWT");
		}

		// You can add extra logic here if you want to check session validity or
		// Principal details

		List<ProductDto> trendingProducts = productService.getTrendingProducts();
		return ResponseEntity.ok(trendingProducts);
	}

}
