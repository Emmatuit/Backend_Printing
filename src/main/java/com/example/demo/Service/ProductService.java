package com.example.demo.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.CartItemDto;
import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SpecificationOptionDTO;
import com.example.demo.Features.ClickedProductHistory;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ClickedProductHistoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.calculations.CalculationBased;
import com.example.demo.model.ImageInfo;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.Subcategory;
import com.example.demo.model.UserEntity;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
	private ProductRepository productRepository;
	private SubcategoryRepository subcategoryRepository;
	private CartRepository cartRepository;
	private CartItemRepository cartItemRepository;
	private CalculationBased calculationBased;
	private SpecificationRepository specificationRepository;
	private UserRepository userRepository;

	@Autowired
	private ImagekitService imagekitService;

	@Autowired
	private ClickedProductHistoryRepository clickedProductHistoryRepository;

	private SpecificationOptionRepository specificationOptionRepository;


	// Setter for CalculationBased
		@Autowired
		public void setCalculationBased(CalculationBased calculationBased) {
			this.calculationBased = calculationBased;
		}

		// Setter for CartItemRepository
		@Autowired
		public void setCartItemRepository(CartItemRepository cartItemRepository) {
			this.cartItemRepository = cartItemRepository;
		}

		// Setter for CartRepository
		@Autowired
		public void setCartRepository(CartRepository cartRepository) {
			this.cartRepository = cartRepository;
		}

		// Setter for ProductRepository
		@Autowired
		public void setProductRepository(ProductRepository productRepository) {
			this.productRepository = productRepository;
		}

		// Setter for SpecificationOptionRepository
		@Autowired
		public void setSpecificationOptionRepository(SpecificationOptionRepository specificationOptionRepository) {
			this.specificationOptionRepository = specificationOptionRepository;
		}

		// Setter for SpecificationRepository
		@Autowired
		public void setSpecificationRepository(SpecificationRepository specificationRepository) {
			this.specificationRepository = specificationRepository;
		}

		// Setter for SubcategoryRepository
		@Autowired
		public void setSubcategoryRepository(SubcategoryRepository subcategoryRepository) {
			this.subcategoryRepository = subcategoryRepository;
		}

		@Autowired
		public void setUserRepository(UserRepository userRepository) {
			this.userRepository = userRepository;
		}


	// Method to save a new product
	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	// ============================================================================//
	public BigDecimal calculateCartSubtotal(List<CartItemDto> cartItems) {
		BigDecimal subtotal = BigDecimal.ZERO;

		for (CartItemDto item : cartItems) {
			Long productId = item.getProduct().getId();
			Integer quantity = item.getSelectedQuantity();

			List<Long> selectedOptionIds = item.getSelectedOptions().stream().map(SpecificationOptionDTO::getId)
					.collect(Collectors.toList());

			BigDecimal itemTotal = calculateTotalPrice(productId, quantity, selectedOptionIds);
			subtotal = subtotal.add(itemTotal);
		}

		return subtotal.setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal calculateTotalPrice(Long productId, Integer selectedQuantity, List<Long> selectedOptionIds) {
	    // Fetch the product
	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

	    // Validate quantity
	    List<Integer> validQuantities = calculationBased.generateQuantityOptions(productId);
	    if (!validQuantities.contains(selectedQuantity)) {
	        throw new RuntimeException("Invalid quantity selected. Valid quantities are: " + validQuantities);
	    }

	    // Calculate the factor based on minQuantity steps
	    int factor = selectedQuantity / product.getMinOrderquantity();

	    // Base price scaling
	    BigDecimal scaledBasePrice = product.getBaseprice().multiply(BigDecimal.valueOf(factor));

	    BigDecimal scaledAdditionalCost = BigDecimal.ZERO;

	    // Process selected options
	    if (selectedOptionIds != null && !selectedOptionIds.isEmpty()) {
	        // Fetch specifications and their options
	        List<Specification> productSpecifications = specificationRepository.findByProductId(productId);
	        List<SpecificationOption> allOptions = productSpecifications.stream()
	                .flatMap(spec -> spec.getOptions().stream())
	                .collect(Collectors.toList());

	        // Find selected options
	        List<SpecificationOption> selectedOptions = allOptions.stream()
	                .filter(option -> selectedOptionIds.contains(option.getId()))
	                .collect(Collectors.toList());

	        // Validate single option per specification
	        Map<Long, List<SpecificationOption>> groupedBySpec = selectedOptions.stream()
	                .collect(Collectors.groupingBy(option -> option.getSpecification().getId()));

	        for (Map.Entry<Long, List<SpecificationOption>> entry : groupedBySpec.entrySet()) {
	            if (entry.getValue().size() > 1) {
	                throw new RuntimeException("Duplicate options selected for specification ID: " + entry.getKey());
	            }
	        }

	        // Sum option prices, scaling each by factor
	        scaledAdditionalCost = selectedOptions.stream()
	                .map(option -> (option.getPrice() != null ? option.getPrice() : BigDecimal.ZERO)
	                        .multiply(BigDecimal.valueOf(factor)))
	                .reduce(BigDecimal.ZERO, BigDecimal::add);
	    }

	    // Final total price = scaled base price + scaled additional cost
	    BigDecimal totalPrice = scaledBasePrice.add(scaledAdditionalCost)
	            .setScale(2, RoundingMode.HALF_UP);

	    // Log for debugging
	    log.info("Product ID: {}, Base Price: {}, Scaled Base Price: {}, Scaled Additional Cost: {}, Quantity: {}, Total: {}",
	            productId, product.getBaseprice(), scaledBasePrice, scaledAdditionalCost, selectedQuantity, totalPrice);

	    return totalPrice;
	}

//========================================================================================================//

	// ================================================================================================//
	public boolean existsByNameAndSubcategoryId(String name, Long subcategoryId) {
		return productRepository.existsByNameAndSubcategoryId(name, subcategoryId);
	}

	@Transactional
	public ProductDto getProductAndIncrementViews(Long id) {
		Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

		product.setViews(product.getViews() + 1);

		// No explicit save needed because of @Transactional, but you can call save if
		// you prefer:
		// productRepository.save(product);

		return mapToDto(product);
	}

	// Get product by ID
	public Product getProductById(Long productId) {
		return productRepository.findById(productId).orElse(null);
	}

	public List<Product> getProductsBySubcategory(Long subcategoryId) {
		Subcategory subcategory = subcategoryRepository.findById(subcategoryId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid subcategory ID"));
		return productRepository.findBySubcategoryAndIsDeletedFalse(subcategory);
	}

	public Page<ProductDto> getSimilarProducts(Long productId, int page, int size) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		// Calculate price range with proper scaling
		BigDecimal basePrice = product.getBaseprice().add(BigDecimal.ONE).setScale(2, RoundingMode.HALF_UP);

		BigDecimal maxPrice = basePrice.add(new BigDecimal("1000")).setScale(2, RoundingMode.HALF_UP);

		Pageable pageable = PageRequest.of(page, size);

		Page<Product> similarProducts = productRepository.findSimilarProductsNotDeleted(product.getCategory().getId(), basePrice,
				maxPrice, product.getId(), pageable);

		return similarProducts.map(this::mapToDto);
	}

	public List<ProductDto> getTrendingProducts() {
		List<Product> trending = productRepository.findTop10ByIsDeletedFalseOrderByViewsDesc();
		return trending.stream().map(this::mapToDto).collect(Collectors.toList());
	}



	private ProductDto mapToDto(Product product) {
		ProductDto dto = new ProductDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setBasePrice(product.getBaseprice());
		dto.setMinOrderQuantity(product.getMinOrderquantity());
		dto.setMaxQuantity(product.getMaxQuantity());
		dto.setIncrementStep(product.getIncrementStep());
		dto.setEncryptedImages(product.getEncryptedImages());
		dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
		dto.setSubcategoryId(product.getSubcategory() != null ? product.getSubcategory().getId() : null);

		// ✅ Add these two lines
		dto.setViews(product.getViews());
		dto.setCreatedAt(product.getCreatedAt());

		return dto;
	}

	@Transactional
	public void mergeSessionHistoryWithUser(String email, String sessionId) {
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		if (sessionId == null) {
			return;
		}

		List<ClickedProductHistory> sessionHistory = clickedProductHistoryRepository.findBySessionId(sessionId);

		for (ClickedProductHistory entry : sessionHistory) {
			entry.setUser(user);
			entry.setSessionId(null);
		}

		clickedProductHistoryRepository.saveAll(sessionHistory);
	}

	// Method to save product
	public Product saveProduct(Product product) {
		return productRepository.save(product); // Save the product to the database
	}

	public List<ProductDto> searchProductsByName(String name) {
		List<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
		return products.stream().map(this::mapToDto) // Use your defined method
				.toList();
	}

	private void deleteFromImageKitSafely(String fileId) {
	    if (fileId != null && !fileId.isEmpty()) {
	        try {
	            imagekitService.deleteFileFromImageKit(fileId);
	        } catch (Exception e) {
	            System.err.println("Failed to delete from ImageKit: " + fileId);
	        }
	    }
	}


	@Transactional
	public void deleteProductPermanently(Long productId) {
	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

	    // 1. Delete Product Images from ImageKit
	    if (product.getEncryptedImages() != null) {
	        for (ImageInfo imageInfo : product.getEncryptedImages()) {
	            String fileId = imageInfo.getFileId(); // ✅ Use fileId, not URL
	            if (fileId != null && !fileId.isEmpty()) {
	                deleteFromImageKitSafely(fileId);
	            }
	        }
	    }

	    // 2. Delete SpecificationOption Images from ImageKit
	    if (product.getSpecifications() != null) {
	        for (Specification spec : product.getSpecifications()) {
	            if (spec.getOptions() != null) {
	                for (SpecificationOption option : spec.getOptions()) {
	                    ImageInfo imageInfo = option.getImage(); // Get the ImageInfo object
	                    if (imageInfo != null && imageInfo.getUrl() != null) {
	                        String fileId = extractFileIdFromUrl(imageInfo.getUrl());
	                        if (fileId != null && !fileId.isEmpty()) {
	                            deleteFromImageKitSafely(fileId);
	                        }
	                    }
	                }
	            }
	        }
	    }

	    log.info("⚠ Deleting dependent records for product {}", productId);

	    productRepository.deleteSpecOptionsByProductId(productId);
	    productRepository.deleteSpecificationsByProductId(productId);
	    productRepository.deleteEncryptedImagesByProductId(productId);

	    log.info("🔥 Deleting product {}", productId);
	    productRepository.hardDeleteById(productId);
	    log.info("✅ Product deleted: {}", productId);
	}


	private String extractFileIdFromUrl(String imageUrl) {
	    if (imageUrl == null || imageUrl.isEmpty()) {
			return null;
		}
	    return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
	}



}
