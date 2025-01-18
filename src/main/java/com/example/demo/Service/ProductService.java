package com.example.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.Repository.SubcategoryRepository;
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
	private SpecificationRepository specificationRepository;

	// Method to save a new product
	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	// Calculate total price for the entire cart
	public Double calculateCartTotalPrice(String sessionId) {
		// Retrieve the cart using the session ID
		Cart cart = cartRepository.findBySessionId(sessionId).orElseThrow(() -> new RuntimeException("Cart not found"));

		// Calculate the total price for all items in the cart
		return cart.getItems().stream()
				.mapToDouble(item -> calculateTotalPrice(item.getProductId(), item.getQuantity())).sum();
	}

	public Double calculateTotalPrice(Long productId, Integer selectedQuantity) {
		// Fetch the product from the database using the productId
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		// Get the base price of the product
		Double basePrice = product.getBaseprice();

		// Calculate the additional cost of the selected options

		// Calculate the total price
		double totalPrice = basePrice * selectedQuantity;
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
