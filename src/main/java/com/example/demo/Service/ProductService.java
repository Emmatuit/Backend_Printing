package com.example.demo.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.Repository.SubcategoryRepository;
import com.example.demo.calculations.CalculationBased;
import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.Subcategory;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SubcategoryRepository subcategoryRepository;

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CalculationBased calculationBased;
	
	@Autowired
	private SpecificationRepository specificationRepository;
	
	@Autowired
	private SpecificationOptionRepository  specificationOptionRepository;
	
	 private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	// Method to save a new product
	public Product addProduct(Product product) {
		return productRepository.save(product);
	}


//	public Double calculateTotalPrice(Long productId, Integer selectedQuantity, Long selectedOptionId) {
//	    // Fetch the product from the database using the productId
//	    Product product = productRepository.findById(productId)
//	            .orElseThrow(() -> new RuntimeException("Product not found"));
//
//	    // Get the base price of the product
//	    Double basePrice = product.getBaseprice();
//	    log.warn("Base price for productId {}: {}", productId, basePrice);
//
//	    // Initialize the additional cost
//	    double additionalCost = 0.0;
//
//	    // If a selectedOptionId is provided, fetch its price
//	    if (selectedOptionId != null) {
//	        // Fetch the specification option based on the selectedOptionId
//	        SpecificationOption selectedOption = specificationOptionRepository.findById(selectedOptionId)
//	                .orElseThrow(() -> new RuntimeException("Specification option not found"));
//
//	        // Get the price of the selected option
//	        additionalCost = selectedOption.getPrice();
//	        log.warn("Selected specification option: {}, Price: {}", selectedOption.getName(), additionalCost);
//	    } else {
//	        log.debug("No specification option selected for productId {}", productId);
//	    }
//
//	    // Calculate the total price (base price + additional cost) * selected quantity
//	    double totalPrice = (basePrice + additionalCost) * selectedQuantity;
//
//	    // Log the total calculated price
//	    log.warn("Calculated total price for productId {}: {}", productId, totalPrice);
//
//	    return totalPrice;
//	}
	public Double calculateTotalPrice(Long productId, Integer selectedQuantity, List<Long> selectedOptionIds) {
	    // Fetch the product using the productId
	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

	    // Generate valid quantity options for the product
	    List<Integer> validQuantities = calculationBased.generateQuantityOptions(productId);

	    // Validate that the selected quantity is valid
	    if (!validQuantities.contains(selectedQuantity)) {
	        throw new RuntimeException("Invalid quantity selected. Valid quantities are: " + validQuantities);
	    }

	    // Get the base price of the product
	    Double basePrice = product.getBaseprice();

	    // Initialize the additional cost
	    double additionalCost = 0.0;

	    // Ensure that selectedOptionIds is not null or empty
	    if (selectedOptionIds != null && !selectedOptionIds.isEmpty()) {
	        // Fetch all specifications for the product
	        List<Specification> productSpecifications = specificationRepository.findByProductId(productId);

	        // Flatten all specification options linked to this product
	        List<SpecificationOption> allOptions = productSpecifications.stream()
	                .flatMap(spec -> spec.getOptions().stream())
	                .collect(Collectors.toList());

	        // Map selected options by their IDs for validation
	        List<SpecificationOption> selectedOptions = allOptions.stream()
	                .filter(option -> selectedOptionIds.contains(option.getId()))
	                .collect(Collectors.toList());

	        // Validate that only one option is selected per specification
	        Map<Long, List<SpecificationOption>> groupedBySpec = selectedOptions.stream()
	                .collect(Collectors.groupingBy(option -> option.getSpecification().getId()));

	        for (Map.Entry<Long, List<SpecificationOption>> entry : groupedBySpec.entrySet()) {
	            if (entry.getValue().size() > 1) {
	                throw new RuntimeException("Only one option can be selected per specification for specification ID: " + entry.getKey());
	            }
	        }

	        // Calculate the additional cost by summing up the selected options' prices
	        additionalCost = selectedOptions.stream()
	                .mapToDouble(SpecificationOption::getPrice)
	                .sum();

	        // Validate: Ensure all selectedOptionIds are valid
	        if (selectedOptions.size() != selectedOptionIds.size()) {
	            throw new RuntimeException("One or more selected option IDs are invalid for the given product.");
	        }
	    }

	    // Calculate the total price
	    double totalPrice = (basePrice + additionalCost) * selectedQuantity;

	    // Log the details for debugging
	    log.info("Product ID: {}, Base Price: {}, Additional Cost: {}, Selected Quantity: {}, Total Price: {}",
	            productId, basePrice, additionalCost, selectedQuantity, totalPrice);

	    return totalPrice;
	}



	public boolean existsByNameAndSubcategoryId(String name, Long subcategoryId) {
		return productRepository.existsByNameAndSubcategoryId(name, subcategoryId);
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

	// Method to save product
	public Product saveProduct(Product product) {
		return productRepository.save(product); // Save the product to the database
	}
	
	 // Method to add specification to a product
//    public Product addSpecificationToProduct(Long productId, Long specificationId, List<SpecificationOption> options) {
//        // Find the product by ID
//        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
//
//        // Find the specification by ID
//        Specification specification = specificationRepository.findById(specificationId)
//                .orElseThrow(() -> new RuntimeException("Specification not found"));
//
//        // Set the options for the specification
//        specification.setOptions(options);
//
//        // Save the updated specification
//        specificationRepository.save(specification);
//
//        // Add the specification to the product
//        product.getSpecifications().add(specification);
//        productRepository.save(product);
//
//        return product;
//    }

}
