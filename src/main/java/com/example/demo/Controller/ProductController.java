package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.SpecificationService;
import com.example.demo.Service.SubcategoryService;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.Subcategory;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private SubcategoryService subcategoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryService categoryService;
	
	 @Autowired
	 private SpecificationService specificationService;


	@PostMapping("/{subcategoryId}/addProduct")
	public ResponseEntity<ProductDto> addProductToSubcategory(@PathVariable Long subcategoryId,
			@RequestParam("name") String name, @RequestParam("description") String description,
			@RequestParam("baseprice") Double baseprice, @RequestParam("minOrderQuantity") Integer minOrderQuantity,
			@RequestParam("maxQuantity") Integer maxQuantity, @RequestParam("incrementStep") Integer incrementStep,
			@RequestParam("images") List<MultipartFile> images) throws IOException {

		// Directory to save images (adjust as needed)
		String uploadDir = "C:\\Users\\PC\\Music\\ecommerce1234";

		// Save images and get the paths
		List<String> imagePaths = new ArrayList<>();
		for (MultipartFile image : images) {
			if (!image.isEmpty()) {
				String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
				Path filePath = Paths.get(uploadDir, fileName);
				Files.write(filePath, image.getBytes());
				imagePaths.add(filePath.toString());
			}
		}

		// Fetch the subcategory
		Subcategory subcategory = subcategoryService.getSubcategoryById(subcategoryId);
		if (subcategory == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		// Create and save the product
		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setBaseprice(baseprice);
		product.setMinOrderquantity(minOrderQuantity);
		product.setMaxQuantity(maxQuantity);
		product.setIncrementStep(incrementStep);
		product.setEncryptedImages(imagePaths); // Store the paths to the images
		product.setSubcategory(subcategory); // Link product to subcategory

		// Also, set the category of the product from the subcategory
		product.setCategory(subcategory.getCategory()); // Set the category

		// Save product and return the response
		product = productService.saveProduct(product);

		ProductDto productDto = new ProductDto();
		productDto.setId(product.getId());
		productDto.setName(product.getName());
		productDto.setDescription(product.getDescription());
		productDto.setBasePrice(product.getBaseprice());
		productDto.setMinOrderQuantity(product.getMinOrderquantity());
		productDto.setMaxQuantity(product.getMaxQuantity());
		productDto.setIncrementStep(product.getIncrementStep());
		productDto.setEncryptedImages(product.getEncryptedImages());
		productDto.setSubcategoryId(product.getSubcategory().getId());
		productDto.setCategoryId(product.getCategory().getId()); // Now this should work

		return new ResponseEntity<>(productDto, HttpStatus.CREATED);
	}

@PostMapping("/calculateTotalPrice")
	public ResponseEntity<Double> calculateTotalPrice(@RequestParam Long productId,
			@RequestParam Integer selectedQuantity) {

		// Fetch the product from the database
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		// Start with the product's base price multiplied by the selected quantity
		Double totalPrice = product.getBaseprice() * selectedQuantity;

		System.out.println("Final Total Price: " + totalPrice);

		return ResponseEntity.ok(totalPrice);
	}

	//Display product based on sub-category
	@GetMapping("/{categoryId}/subcategories/{subcategoryId}/products")
	public ResponseEntity<?> getProductsBySubcategoryAndCategory(@PathVariable Long categoryId,
			@PathVariable Long subcategoryId) {

		try {
			// Fetch products by subcategory and category, returning DTOs
			List<ProductDto> products = categoryService.getProductsBySubcategoryAndCategory(categoryId, subcategoryId);
			return ResponseEntity.ok(products);
		} catch (IllegalArgumentException e) {
			// Handle errors if category or subcategory not found
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}
	
	


}
