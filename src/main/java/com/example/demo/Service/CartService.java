package com.example.demo.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dto.CartItemDto;
import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.SpecificationDTO;
import com.example.demo.Dto.SpecificationOptionDTO;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.SpecificationRepository;
import com.example.demo.calculations.CalculationBased;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;

@Service
public class CartService {

	// Validate that only one option is selected per specification
	public static void validateSelectedOptions(List<SpecificationOption> selectedOptions) {
		Set<Long> specificationIds = selectedOptions.stream().map(option -> option.getSpecification().getId())
				.collect(Collectors.toSet());
		if (specificationIds.size() < selectedOptions.size()) {
			throw new IllegalArgumentException("Only one option can be selected per specification.");
		}
	}

	@Autowired
	private CartRepository cartRepository;

	private SpecificationRepository specificationRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CalculationBased calculationBased;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SpecificationOptionRepository specificationOptionRepository;

	@Autowired
	private SpecificationOptionService specificationOptionService;

	public CartItemDto addItemToCart(String sessionId, CartItemDto cartItemDTO) {
		// Retrieve or create a cart
		Cart cart = cartRepository.findBySessionId(sessionId).orElseGet(() -> cartRepository.save(new Cart(sessionId)));

		// Validate input
		if (cartItemDTO == null || cartItemDTO.getProduct() == null || cartItemDTO.getSelectedOptions() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product or selected options cannot be null.");
		}

		// Fetch product from database
		Product product = productRepository.findById(cartItemDTO.getProduct().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

		// Fetch selected options
		List<SpecificationOption> selectedOptions = cartItemDTO.getSelectedOptions().stream()
				.map(optionDTO -> specificationOptionRepository.findById(optionDTO.getId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								"Selected option with ID " + optionDTO.getId() + " not found")))
				.collect(Collectors.toList());

		// Validate selected options
		try {
			validateSelectedOptions(selectedOptions);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

		// Calculate valid quantity options
		List<Integer> validQuantities = calculationBased.generateQuantityOptions(product.getId());
		int selectedQuantity = cartItemDTO.getSelectedQuantity();

		if (!validQuantities.contains(selectedQuantity)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid quantity selected. Valid quantities are: " + validQuantities);
		}

		// Calculate the total price
		List<Long> selectedOptionIds = selectedOptions.stream().map(SpecificationOption::getId).toList();
		double totalPrice = productService.calculateTotalPrice(product.getId(), selectedQuantity, selectedOptionIds);

		// Create and add cart item
		CartItem cartItem = new CartItem(product, selectedQuantity, totalPrice, selectedOptions);
		cart.addItem(cartItem);
		cartRepository.save(cart); // Ensure cart is saved after modification

		return convertToCartItemDTO(cartItem);
	}

	@Scheduled(cron = "0 0 */1 * * *") // Every hour (use this as a realistic testing interval)
	public void clearExpiredCarts() {
		LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
		List<Cart> expiredCarts = cartRepository.findAll().stream()
				.filter(cart -> cart.getCreatedAt().isBefore(sevenDaysAgo)).collect(Collectors.toList());

		if (!expiredCarts.isEmpty()) {
			cartRepository.deleteAll(expiredCarts);
			System.out.println("Deleted expired carts: " + expiredCarts.size());
		} else {
			System.out.println("No expired carts to delete.");
		}
	}

	private CartItemDto convertToCartItemDTO(CartItem cartItem) {
		CartItemDto cartItemDTO = new CartItemDto();
		cartItemDTO.setProduct(convertToProductDTO(cartItem.getProduct()));
		cartItemDTO.setSelectedOptions(cartItem.getSelectedOptions().stream().map(this::convertToSpecificationOptionDTO)
				.collect(Collectors.toList()));
		cartItemDTO.setSelectedQuantity(cartItem.getSelectedQuantity());
		return cartItemDTO;
	}

	private ProductDto convertToProductDTO(Product product) {
		ProductDto productDTO = new ProductDto();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setDescription(product.getDescription());
		productDTO.setBasePrice(product.getBaseprice());
		productDTO.setMinOrderQuantity(product.getMinOrderquantity());
		productDTO.setMaxQuantity(product.getMaxQuantity());
		productDTO.setIncrementStep(product.getIncrementStep());
		return productDTO;
	}

	private SpecificationDTO convertToSpecificationDto(Specification specification) {
		List<SpecificationOptionDTO> optionDTOs = specification.getOptions() != null ? specification.getOptions()
				.stream().map(this::convertToSpecificationOptionDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		return new SpecificationDTO(specification.getId(), specification.getName(), optionDTOs // ✅ Include options
		);
	}

//	// Helper method to find a cart item by productId
//	private CartItem findCartItem(Cart cart, Long productId) {
//		return cart.getItems().stream().filter(item -> item.getProductId().equals(productId)).findFirst().orElseThrow(
//				() -> new IllegalArgumentException("Item not found in the cart with Product ID: " + productId));
//	}
//
//	public CartDto getCart(String sessionId) {
//		// Attempt to fetch the cart
//		Cart cart = cartRepository.findBySessionId(sessionId).orElse(null);
//
//		// If no cart exists, return an empty cart
//		if (cart == null) {
//			CartDto emptyCart = new CartDto();
//			emptyCart.setSessionId(sessionId);
//			emptyCart.setItems(Collections.emptyList()); // Empty list for items
//			return emptyCart;
//		}
//
//		// Convert Cart to CartDto
//		CartDto cartDto = new CartDto();
//		cartDto.setSessionId(cart.getSessionId());
//
//		List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
//			// Fetch product details (name and description)
//			Product product = productRepository.findById(item.getProductId())
//					.orElseThrow(() -> new RuntimeException("Product not found"));
//
//			// Create CartProductDisplay for product details
//			CartProductDisplay productDisplay = new CartProductDisplay(product.getName(), product.getDescription(),
//					product.getBaseprice());
//
//			// Create CartItemDto with product details and selected quantity
//			return new CartItemDto(item.getProductId(), item.getQuantity(), productDisplay, item.getPrice(), null);
//		}).collect(Collectors.toList());
//
//		cartDto.setItems(itemDtos);
//
//		return cartDto;
//	}

	// ✅ Convert SpecificationOption -> SpecificationOptionDTO
	private SpecificationOptionDTO convertToSpecificationOptionDto(SpecificationOption option) {
		return new SpecificationOptionDTO(option.getId(), option.getName(), option.getPrice(), option.getImage());
	}

//	public List<CartItemDto> getAllCartItems(String sessionId) {
//        // Fetch the cart by session ID
//        Cart cart = cartRepository.findBySessionId(sessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Cart not found for session ID: " + sessionId));
//
//        // Map cart items to DTOs
//        return cart.getItems().stream()
//                .map(cartItem -> {
//                    CartItemDto cartItemDto = new CartItemDto();
//                    cartItemDto.setProductId(cartItem.getProductId());
//                    cartItemDto.setSelectedQuantity(cartItem.getQuantity());
//                    cartItemDto.setCalculatedPrice(cartItem.getPrice());
//
//                    // Fetch product details and populate product display
//                    Product product = productRepository.findById(cartItem.getProductId())
//                            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + cartItem.getProductId()));
//
//                    CartProductDisplay productDisplay = new CartProductDisplay();
//                    productDisplay.setName(product.getName());
//                    productDisplay.setDescription(product.getDescription());
//                    productDisplay.setBaseprice(product.getBaseprice());
//
//                    cartItemDto.setProductDisplay(productDisplay);
//
//                    return cartItemDto;
//                })
//                .collect(Collectors.toList());
//    }

	// Helper method to convert SpecificationOption to SpecificationOptionDTO
	private SpecificationOptionDTO convertToSpecificationOptionDTO(SpecificationOption option) {
		SpecificationOptionDTO optionDTO = new SpecificationOptionDTO();
		optionDTO.setId(option.getId());
		optionDTO.setName(option.getName());
		optionDTO.setImage(option.getImage());
		optionDTO.setPrice(option.getPrice());
		return optionDTO;
	}

	// Method to get all cart items for a session
	public List<CartItemDto> getAllCartItems(String sessionId) {
		// Fetch the cart by sessionId
		Cart cart = cartRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new IllegalArgumentException("Cart not found for session ID: " + sessionId));

		// Map the CartItems to CartItemDtos
		return cart.getItems().stream().map(this::toDto) // Call the internal mapper method
				.collect(Collectors.toList());
	}

	public Cart getCartBySessionId(String sessionId) {
		return cartRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new RuntimeException("Cart not found for session: " + sessionId));
	}

	private CartItemDto toDto(CartItem cartItem) {
		CartItemDto dto = new CartItemDto();

		// Convert CartItem's product to ProductDto (including specifications)
		Product product = cartItem.getProduct();
		ProductDto productDto = new ProductDto(product.getId(), product.getName(), product.getDescription(),
				product.getBaseprice(), product.getMinOrderquantity(), product.getMaxQuantity(),
				product.getIncrementStep(), product.getSubcategory() != null ? product.getSubcategory().getId() : null, // Prevent
																														// null
				product.getCategory() != null ? product.getCategory().getId() : null, // Prevent null
				product.getEncryptedImages(), product.getSpecifications() != null ? product.getSpecifications().stream()
						.map(this::convertToSpecificationDto).collect(Collectors.toList()) : Collections.emptyList() // Prevent
																														// null
																														// issues
		);

		// Set the converted productDto
		dto.setProduct(productDto);

		// Convert selected options (if any) to SpecificationOptionDTO
		List<SpecificationOptionDTO> selectedOptions = cartItem.getSelectedOptions() != null ? cartItem
				.getSelectedOptions().stream().map(this::convertToSpecificationOptionDto).collect(Collectors.toList())
				: Collections.emptyList(); // Prevent null issues

		dto.setSelectedOptions(selectedOptions);

		// Set the selected quantity
		dto.setSelectedQuantity(cartItem.getSelectedQuantity());

		return dto;
	}

//	// Method to convert CartItem to CartItemDto (Mapper)
//	private CartItemDto toDto(CartItem cartItem) {
//		CartItemDto dto = new CartItemDto();
//		// Convert CartItem's product to ProductDto
//		dto.setProduct(new ProductDto(cartItem.getProduct().getId(), cartItem.getProduct().getName(),
//				cartItem.getProduct().getDescription(), cartItem.getProduct().getBaseprice(),
//				cartItem.getProduct().getMinOrderquantity(), cartItem.getProduct().getMaxQuantity(),
//				cartItem.getProduct().getIncrementStep(), cartItem.getProduct().getSubcategory().getId(),
//				cartItem.getProduct().getCategory().getId(), cartItem.getProduct().getEncryptedImages()));
//
//		// Convert selected options (if any) to SpecificationOptionDto
//		dto.setSelectedOptions(
//				cartItem.getSelectedOptions().stream().map(option -> new SpecificationOptionDTO(option.getId(),
//						option.getName(), option.getPrice(), option.getImage())).collect(Collectors.toList()));
//
//		// Set the selected quantity
//		dto.setSelectedQuantity(cartItem.getSelectedQuantity());
//
//		return dto;
//	}

// // Scheduled to run daily at midnight
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void cleanupOldCarts() {
//        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1); // 24 hours ago
//        List<Cart> oldCarts = cartRepository.findByLastUpdatedBefore(cutoffTime);
//
//        if (!oldCarts.isEmpty()) {
//            cartRepository.deleteAll(oldCarts);
//            System.out.println("Deleted " + oldCarts.size() + " old carts.");
//        }
//

//    // Scheduled method to delete carts older than 2 minutes
//    @Scheduled(fixedRate = 120000) // Runs every 2 minutes
//    public void deleteOldCarts() {
//        System.out.println("Running scheduled task to delete old carts...");
//
//        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(2);
//
//        List<Cart> oldCarts = cartRepository.findAll().stream()
//                .filter(cart -> cart.getCreatedAt().isBefore(expirationTime))
//                .toList();
//
//        if (!oldCarts.isEmpty()) {
//            cartRepository.deleteAll(oldCarts);
//            System.out.println("Deleted " + oldCarts.size() + " old carts.");
//        } else {
//            System.out.println("No old carts found to delete.");
//        }
//    }

//	public void removeFromCart(String sessionId, Long productId) {
//		// Log the input parameters for debugging
//		System.out.println("Session ID: " + sessionId + ", Product ID: " + productId);
//
//		// Retrieve the cart by session ID and handle if not found
//		Cart cart = cartRepository.findBySessionId(sessionId)
//				.orElseThrow(() -> new RuntimeException("Cart not found for session: " + sessionId));
//
//		// Log the cart items before removal
//		System.out.println("Cart items before removal: " + cart.getItems());
//
//		// Find the cart item to remove based on productId
//		CartItem cartItem = findCartItem(cart, productId);
//
//		// Log the cart item to be removed
//		System.out.println("Cart item to remove: " + cartItem);
//
//		// Remove the item from the cart
//		cart.getItems().remove(cartItem);
//
//		// Log the updated cart items
//		System.out.println("Cart items after removal: " + cart.getItems());
//
//		// If the cart is empty after removal, delete the cart, otherwise save the
//		// changes
//		if (cart.getItems().isEmpty()) {
//			cartRepository.delete(cart);
//			System.out.println("Cart was empty and deleted.");
//		} else {
//			cartRepository.save(cart);
//			System.out.println("Cart updated after item removal.");
//		}
//	}

	// Other methods: remove item, clear cart, etc.
}
