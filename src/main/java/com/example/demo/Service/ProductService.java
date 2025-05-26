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

import com.example.demo.Dto.ProductDto;
import com.example.demo.Features.ClickedProductHistory;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ClickedProductHistoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.calculations.CalculationBased;
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
	private ClickedProductHistoryRepository clickedProductHistoryRepository;

	private SpecificationOptionRepository specificationOptionRepository;

	// Method to save a new product
	public Product addProduct(Product product) {
		return productRepository.save(product);
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

	    BigDecimal basePrice = product.getBaseprice();
	    BigDecimal additionalCost = BigDecimal.ZERO;

	    // Process selected options
	    if (selectedOptionIds != null && !selectedOptionIds.isEmpty()) {
	        List<Specification> productSpecifications = specificationRepository.findByProductId(productId);
	        List<SpecificationOption> allOptions = productSpecifications.stream()
	                .flatMap(spec -> spec.getOptions().stream())
	                .collect(Collectors.toList());

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

	        // Sum options' prices using BigDecimal
	        additionalCost = selectedOptions.stream()
	                .map(option -> option.getPrice() != null ? option.getPrice() : BigDecimal.ZERO)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);
	    }

	    // Calculate total price
	    BigDecimal totalPrice = basePrice.add(additionalCost)
	            .multiply(new BigDecimal(selectedQuantity))
	            .setScale(2, RoundingMode.HALF_UP);

	    log.info("Product ID: {}, Base Price: {}, Additional Cost: {}, Quantity: {}, Total: {}",
	            productId, basePrice, additionalCost, selectedQuantity, totalPrice);

	    return totalPrice;
	}

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
	    return productRepository.findBySubcategory(subcategory);
	}

	public Page<ProductDto> getSimilarProducts(Long productId, int page, int size) {
	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found"));

	    // Calculate price range with proper scaling
	    BigDecimal basePrice = product.getBaseprice()
	            .add(BigDecimal.ONE)
	            .setScale(2, RoundingMode.HALF_UP);
	            
	    BigDecimal maxPrice = basePrice
	            .add(new BigDecimal("1000"))
	            .setScale(2, RoundingMode.HALF_UP);

	    Pageable pageable = PageRequest.of(page, size);

	    Page<Product> similarProducts = productRepository.findSimilarProducts(
	            product.getCategory().getId(),
	            basePrice,
	            maxPrice,
	            product.getId(),
	            pageable);

	    return similarProducts.map(this::mapToDto);
	}


	public List<ProductDto> getTrendingProducts() {
		List<Product> trending = productRepository.findTop10ByOrderByViewsDesc();
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
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

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
		List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
		return products.stream().map(this::mapToDto) // Use your defined method
				.toList();
	}

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

}
