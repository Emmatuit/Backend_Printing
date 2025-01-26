package com.example.demo.calculations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.ProductService;
import com.example.demo.model.Product;

@Service
public class CalculationBased {

	@Autowired
	private ProductRepository productRepository;

	private ProductService productService;

	 @Autowired
	    public void setProductService(@Lazy ProductService productService) {
	        this.productService = productService;
}

	// Method to generate quantity options for a product
	public List<Integer> generateQuantityOptions(Long productId) {
		// Fetch the product using the product ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

		List<Integer> options = new ArrayList<>();
		// Generate quantity options based on the product's MinOrderQuantity,
		// MaxQuantity, and IncrementStep
		for (int qty = product.getMinOrderquantity(); qty <= product.getMaxQuantity(); qty += product
				.getIncrementStep()) {
			options.add(qty);
		}
		return options;
	}

}
