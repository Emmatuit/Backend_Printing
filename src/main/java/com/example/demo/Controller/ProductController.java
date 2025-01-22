package com.example.demo.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.example.demo.Imagekit.ImagekitService;
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
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private SpecificationService specificationService;
	
	@Autowired
	private SpecificationOptionService specificationOptionService;
	
	@Autowired
	private ImagekitService imagekitService;


	 @PostMapping("/{subcategoryId}/addProduct")
	 public ResponseEntity<ProductDto> addProductToSubcategory1(
	     @PathVariable("subcategoryId") Long subcategoryId,
	     @RequestParam("name") String name, 
	     @RequestParam("description") String description,
	     @RequestParam("baseprice") Double baseprice, 
	     @RequestParam("minOrderQuantity") Integer minOrderQuantity,
	     @RequestParam("maxQuantity") Integer maxQuantity, 
	     @RequestParam("incrementStep") Integer incrementStep,
	     @RequestParam("images") List<MultipartFile> images, // Product images
	     @RequestParam("specifications") String specificationsJson, // Specifications as JSON
	     @RequestParam("specImages") List<MultipartFile> specImages // Specification images
	 ) throws IOException, InternalServerException, BadRequestException, UnknownException, ForbiddenException, TooManyRequestsException, UnauthorizedException {

	     // Upload product images to ImageKit and collect URLs
	     List<String> imageUrls = new ArrayList<>();
	     for (MultipartFile image : images) {
	         if (!image.isEmpty()) {
	             String imageUrl = imagekitService.uploadFileToProduct(image); // Call the ImageKit upload method
	             imageUrls.add(imageUrl); // Add the URL to the list
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
	     product.setEncryptedImages(imageUrls); // Store the paths to the images
	     product.setSubcategory(subcategory); // Link product to subcategory
	     product.setCategory(subcategory.getCategory()); // Set the category
	     product = productService.saveProduct(product);

	     // Parse and save specifications
	     ObjectMapper mapper = new ObjectMapper();
	     List<SpecificationDTO> specifications = mapper.readValue(specificationsJson, new TypeReference<List<SpecificationDTO>>() {});

	     for (SpecificationDTO specDto : specifications) {
	         Specification specification = new Specification();
	         specification.setName(specDto.getName());
	         specification.setProduct(product);
	         specification = specificationService.saveSpecification(specification);

	         // Save options for each specification
	         int optionIndex = 0;
	         for (SpecificationOptionDTO optionDto : specDto.getOptions()) {
	             SpecificationOption option = new SpecificationOption();
	             option.setName(optionDto.getName());
	             option.setPrice(optionDto.getPrice());

	             // Handle image for each option (from specImages)
	             if (optionIndex < specImages.size()) {
	                 MultipartFile specImage = specImages.get(optionIndex);
	                 if (specImage != null && !specImage.isEmpty()) {
	                     // Upload specImage to ImageKit and get the URL
	                     String specImageUrl = imagekitService.uploadSpecificationImageFile(specImage); // Use the uploadFile method
	                     option.setImage(specImageUrl); // Set the URL in the option
	                 }
	             }

	             option.setSpecification(specification);
	             specificationOptionService.saveSpecificationOption(option);

	             // Increment optionIndex to handle next specification image
	             optionIndex++;
	         }
	     }

	     // Create and return ProductDto
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
	     productDto.setCategoryId(product.getCategory().getId());

	     // Fetch the specifications associated with the product
	     List<Specification> specifications1 = specificationService.getSpecificationsByProduct(product);
	     List<SpecificationDTO> specificationDtos = new ArrayList<>();
	     for (Specification specification : specifications1) {
	         SpecificationDTO specDto = new SpecificationDTO();
	         specDto.setId(specification.getId());
	         specDto.setName(specification.getName());

	         // Fetch the options for this specification
	         List<SpecificationOption> options = specificationOptionService.getSpecificationOptionsBySpecification(specification);
	         List<SpecificationOptionDTO> optionDtos = new ArrayList<>();
	         for (SpecificationOption option : options) {
	             SpecificationOptionDTO optionDto = new SpecificationOptionDTO();
	             optionDto.setId(option.getId());
	             optionDto.setName(option.getName());
	             optionDto.setPrice(option.getPrice());
	             optionDto.setImage(option.getImage()); // Set the image path if any
	             optionDtos.add(optionDto);
	         }
	         specDto.setOptions(optionDtos);
	         specificationDtos.add(specDto);
	     }

	     // Set the specifications in the productDto
	     productDto.setSpecifications(specificationDtos);

	     return new ResponseEntity<>(productDto, HttpStatus.CREATED);
	 }

	 //================================================//
	 @PostMapping("/calculateTotalPrice")
	    public ResponseEntity<Double> calculateTotalPrice(
	            @RequestParam("productId") Long productId,
	            @RequestParam("selectedQuantity") Integer selectedQuantity,
	            @RequestBody List<Long> selectedSpecificationIds) {
	        try {
	            // Call the service to calculate the total price
	            Double totalPrice = productService.calculateTotalPrice(productId, selectedQuantity, selectedSpecificationIds);

	            // Return the calculated total price
	            return ResponseEntity.ok(totalPrice);
	        } catch (RuntimeException e) {
	            // Handle errors and return a bad request with the error message
	            return ResponseEntity.badRequest().body(null);
	        }
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
