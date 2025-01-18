package com.example.demo.calculations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class CalController {

	@Autowired
	private CalculationBased calculationBased;

	// API to get quantity options for a product
	@GetMapping("/{productId}/quantity-options")
	public ResponseEntity<List<Integer>> getQuantityOptions(@PathVariable Long productId) {
		List<Integer> quantityOptions = calculationBased.generateQuantityOptions(productId);
		return ResponseEntity.ok(quantityOptions);
	}
}
