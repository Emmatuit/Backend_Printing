package com.example.demo.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.DesignRequestRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.SpecificationOptionRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.DesignRequest;
import com.example.demo.model.Product;
import com.example.demo.model.Specification;
import com.example.demo.model.SpecificationOption;
import com.example.demo.model.UserEntity;

import jakarta.transaction.Transactional;

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

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SpecificationOptionRepository specificationOptionRepository;

	@Autowired
	private SpecificationOptionService specificationOptionService;

	@Autowired
	private DesignRequestRepository designRequestRepository;

	@Autowired
	private UserRepository userRepository;

//	@Transactional
	public CartItemDto addItemToCart(String sessionId, CartItemDto cartItemDTO, Long designRequestId) {
		if (sessionId == null || sessionId.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session ID is required for guest users.");
		}

		if (cartItemDTO == null || cartItemDTO.getProduct() == null || cartItemDTO.getSelectedOptions() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product or selected options cannot be null.");
		}

		// Retrieve or create cart for session
		Cart cart = cartRepository.findBySessionId(sessionId).orElseGet(() -> cartRepository.save(new Cart(sessionId)));

		// Fetch product
		Product product = productRepository.findById(cartItemDTO.getProduct().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

		// Fetch selected specification options
		List<SpecificationOption> selectedOptions = cartItemDTO.getSelectedOptions().stream()
				.map(optionDTO -> specificationOptionRepository.findById(optionDTO.getId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								"Selected option with ID " + optionDTO.getId() + " not found")))
				.collect(Collectors.toList());

		// Validate options
		validateSelectedOptions(selectedOptions);

		// Validate design request
		DesignRequest designRequest = designRequestRepository.findById(designRequestId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Design Request not found"));

		// Validate quantity
		int selectedQuantity = cartItemDTO.getSelectedQuantity();
		List<Integer> validQuantities = productService.generateQuantityOptions(product.getId());
		if (!validQuantities.contains(selectedQuantity)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid quantity selected. Valid quantities are: " + validQuantities);
		}

		// Calculate total price
		List<Long> selectedOptionIds = selectedOptions.stream().map(SpecificationOption::getId).toList();
		BigDecimal totalPrice = productService.calculateTotalPrice(product.getId(), selectedQuantity,
				selectedOptionIds);

		// Create and add CartItem
		CartItem cartItem = new CartItem(product, selectedQuantity, totalPrice, selectedOptions);
		cartItem.setDesignRequest(designRequest);
		cart.addItem(cartItem);

		// Save cart with new item
		cartRepository.save(cart);

		return convertToCartItemDTO(cartItem);
	}

	// =====================================================================//
	@Transactional
	public CartItemDto addItemToUserCart(UserEntity user, CartItemDto cartItemDTO, Long designRequestId) {
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot be null.");
		}

		if (cartItemDTO == null || cartItemDTO.getProduct() == null || cartItemDTO.getSelectedOptions() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product or selected options cannot be null.");
		}

		// Get or create user's cart
		Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
			Cart newCart = new Cart();
			newCart.setUser(user);
			return cartRepository.save(newCart);
		});

		// Fetch product
		Product product = productRepository.findById(cartItemDTO.getProduct().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

		// Fetch selected specification options
		List<SpecificationOption> selectedOptions = cartItemDTO.getSelectedOptions().stream()
				.map(optionDTO -> specificationOptionRepository.findById(optionDTO.getId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								"Selected option with ID " + optionDTO.getId() + " not found")))
				.collect(Collectors.toList());

		// Validate options
		validateSelectedOptions(selectedOptions);

		// Validate design request
		DesignRequest designRequest = designRequestRepository.findById(designRequestId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Design Request not found"));

		// Validate quantity
		int selectedQuantity = cartItemDTO.getSelectedQuantity();
		List<Integer> validQuantities = productService.generateQuantityOptions(product.getId());
		if (!validQuantities.contains(selectedQuantity)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid quantity selected. Valid quantities are: " + validQuantities);
		}

		// Calculate total price
		List<Long> selectedOptionIds = selectedOptions.stream().map(SpecificationOption::getId).toList();
		BigDecimal totalPrice = productService.calculateTotalPrice(product.getId(), selectedQuantity,
				selectedOptionIds);

		// Create and add CartItem
		CartItem cartItem = new CartItem(product, selectedQuantity, totalPrice, selectedOptions);
		cartItem.setDesignRequest(designRequest);
		cart.addItem(cartItem);

		// Save cart with new item
		cartRepository.save(cart);

		return convertToCartItemDTO(cartItem);
	}

	public void clearCart(UserEntity user) {
		Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found for user"));

		cart.getItems().clear();
		cartRepository.save(cart);
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

	// ✅ Convert SpecificationOption -> SpecificationOptionDTO
	private SpecificationOptionDTO convertToSpecificationOptionDto(SpecificationOption option) {
		return new SpecificationOptionDTO(option.getId(), option.getName(), option.getImage(), option.getPrice() // This
																													// now
																													// returns
																													// ImageInfo
		);
	}

	// Helper method to convert SpecificationOption to SpecificationOptionDTO
	private SpecificationOptionDTO convertToSpecificationOptionDTO(SpecificationOption option) {
		SpecificationOptionDTO optionDTO = new SpecificationOptionDTO();
		optionDTO.setId(option.getId());
		optionDTO.setName(option.getName());
		optionDTO.setImage(option.getImage()); // Now sets ImageInfo object
		optionDTO.setPrice(option.getPrice());
		return optionDTO;
	}

	public List<CartItemDto> getAllCartItems(String sessionId) {
		// Fetch the cart by sessionId (return empty if not found)
		Cart cart = getCartBySessionId(sessionId);

		// If cart is not found OR cart has no items, return an empty list
		if (cart == null || cart.getItems().isEmpty()) {
			return Collections.emptyList();
		}
		// Convert cart items to DTOs
		return cart.getItems().stream().map(this::toDto).collect(Collectors.toList());
	}

	public List<CartItemDto> getAllCartItemsByUser(UserEntity user) {
		Cart cart = cartRepository.findByUser(user).orElse(null); // Return null if no cart is found

		if (cart == null || cart.getItems().isEmpty()) {
			return Collections.emptyList();
		}

		return cart.getItems().stream().map(this::toDto).collect(Collectors.toList());
	}

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

	public Cart getCartBySessionId(String sessionId) {
		return cartRepository.findBySessionId(sessionId).orElse(null);
	}

	public Cart getCartByUser(UserEntity user) {
		return cartRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getEmail()));
	}

	@Transactional
	public void mergeSessionCartWithUser(String email, String sessionId) {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User with Email not found"));

		System.out.println("🔹 Merging cart for user: " + email);
		System.out.println("🔹 Provided sessionId: " + sessionId);

		if (sessionId == null) {
			System.out.println("❌ No session ID provided, skipping merge.");
			return;
		}

		Optional<Cart> sessionCartOpt = cartRepository.findBySessionId(sessionId);
		if (sessionCartOpt.isEmpty()) {
			System.out.println("❌ No cart found for sessionId: " + sessionId);
			return;
		}

		Cart sessionCart = sessionCartOpt.get();
		System.out.println("✅ Found cart for sessionId: " + sessionId);

		Cart userCart = cartRepository.findByUser(user).orElseGet(() -> {
			Cart newCart = new Cart();
			newCart.setUser(user);
			return cartRepository.save(newCart);
		});

		userCart.getItems().addAll(sessionCart.getItems());
		sessionCart.getItems().forEach(item -> item.setCart(userCart));

		cartRepository.save(userCart);
		cartRepository.delete(sessionCart); // Clear session cart after merge

		System.out.println("✅ Cart merged successfully for user: " + email);
	}

	private CartItemDto toDto(CartItem cartItem) {
		CartItemDto dto = new CartItemDto();

		// Convert CartItem's product to ProductDto (including specifications)
		Product product = cartItem.getProduct();
		ProductDto productDto = new ProductDto(product.getId(), product.getName(), product.getDescription(),
				product.getBaseprice(), product.getMinOrderquantity(), product.getMaxQuantity(),
				product.getIncrementStep(), product.getSubcategory() != null ? product.getSubcategory().getId() : null,
				product.getCategory() != null ? product.getCategory().getId() : null, product.getEncryptedImages(),
				product.getSpecifications() != null ? product.getSpecifications().stream()
						.map(this::convertToSpecificationDto).collect(Collectors.toList()) : Collections.emptyList(),
				product.getViews(), // Added views here
				product.getCreatedAt()
		// Added createdAt here
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

		// Get the selected option IDs (from selectedOptions)
		List<Long> selectedOptionIds = cartItem.getSelectedOptions().stream().map(SpecificationOption::getId)
				.collect(Collectors.toList());

		// Calculate the total price and set it
		BigDecimal calculatedPrice = productService.calculateTotalPrice(cartItem.getProduct().getId(),
				cartItem.getSelectedQuantity(), selectedOptionIds // Pass the correct List<Long> here
		);
		dto.setTotalPrice(calculatedPrice); // Set the total price

		return dto;
	}

}
