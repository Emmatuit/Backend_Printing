package com.example.demo.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.Dto.CartDto;
import com.example.demo.Dto.CartItemDto;
import com.example.demo.Dto.CartProductDisplay;
import com.example.demo.Repository.CartItemRepository;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.calculations.CalculationBased;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CalculationBased calculationBased;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	public void addToCart(String sessionId, CartItemDto cartItemDTO) {
	    // Find or create cart
	    Cart cart = cartRepository.findBySessionId(sessionId)
	            .orElseGet(() -> cartRepository.save(new Cart(sessionId)));

	    // Check for null values in DTO
	    if (cartItemDTO.getSelectedQuantity() == null || cartItemDTO.getSelectedQuantity() <= 0) {
	        throw new IllegalArgumentException("Quantity must be provided and greater than 0");
	    }

	    // Fetch the product and validate the selected quantity
	    Product product = productRepository.findById(cartItemDTO.getProductId())
	            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

	    // Generate valid quantity options
	    List<Integer> validQuantities = calculationBased.generateQuantityOptions(product.getId());
	    if (!validQuantities.contains(cartItemDTO.getSelectedQuantity())) {
	        throw new IllegalArgumentException("Selected quantity is invalid for the product");
	    }

	    // Check if the product is already in the cart
	    boolean productExists = cart.getItems().stream()
	            .anyMatch(item -> item.getProductId().equals(cartItemDTO.getProductId()));

	    if (productExists) {
	        throw new IllegalArgumentException("This product is already in your cart");
	    }

	    // Ensure selectedOptionIds is not null
	    List<Long> selectedOptionIds = cartItemDTO.getSelectedOptionIds();
	    if (selectedOptionIds == null) {
	        selectedOptionIds = Collections.emptyList(); // Use an empty list if no options are selected
	    }

	    // Calculate the price
	    Double calculatedPrice = productService.calculateTotalPrice(
	            cartItemDTO.getProductId(),
	            cartItemDTO.getSelectedQuantity(),
	            selectedOptionIds // Pass the selected option IDs here
	    );

	    // Map DTO to CartItem and add to cart
	    CartItem cartItem = new CartItem();
	    cartItem.setProductId(cartItemDTO.getProductId());
	    cartItem.setQuantity(cartItemDTO.getSelectedQuantity());
	    cartItem.setPrice(calculatedPrice);

	    cart.addItem(cartItem);
	    cartRepository.save(cart); // Save the cart and its items
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

	// Helper method to find a cart item by productId
	private CartItem findCartItem(Cart cart, Long productId) {
		return cart.getItems().stream().filter(item -> item.getProductId().equals(productId)).findFirst().orElseThrow(
				() -> new IllegalArgumentException("Item not found in the cart with Product ID: " + productId));
	}

	public CartDto getCart(String sessionId) {
		// Attempt to fetch the cart
		Cart cart = cartRepository.findBySessionId(sessionId).orElse(null);

		// If no cart exists, return an empty cart
		if (cart == null) {
			CartDto emptyCart = new CartDto();
			emptyCart.setSessionId(sessionId);
			emptyCart.setItems(Collections.emptyList()); // Empty list for items
			return emptyCart;
		}

		// Convert Cart to CartDto
		CartDto cartDto = new CartDto();
		cartDto.setSessionId(cart.getSessionId());

		List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
			// Fetch product details (name and description)
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			// Create CartProductDisplay for product details
			CartProductDisplay productDisplay = new CartProductDisplay(product.getName(), product.getDescription(),
					product.getBaseprice());

			// Create CartItemDto with product details and selected quantity
			return new CartItemDto(item.getProductId(), item.getQuantity(), productDisplay, item.getPrice());
		}).collect(Collectors.toList());

		cartDto.setItems(itemDtos);

		return cartDto;
	}

	public Cart getCartBySessionId(String sessionId) {
		return cartRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new RuntimeException("Cart not found for session: " + sessionId));
	}
	
	public List<CartItemDto> getAllCartItems(String sessionId) {
        // Fetch the cart by session ID
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for session ID: " + sessionId));

        // Map cart items to DTOs
        return cart.getItems().stream()
                .map(cartItem -> {
                    CartItemDto cartItemDto = new CartItemDto();
                    cartItemDto.setProductId(cartItem.getProductId());
                    cartItemDto.setSelectedQuantity(cartItem.getQuantity());
                    cartItemDto.setCalculatedPrice(cartItem.getPrice());

                    // Fetch product details and populate product display
                    Product product = productRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + cartItem.getProductId()));

                    CartProductDisplay productDisplay = new CartProductDisplay();
                    productDisplay.setName(product.getName());
                    productDisplay.setDescription(product.getDescription());
                    productDisplay.setBaseprice(product.getBaseprice());

                    cartItemDto.setProductDisplay(productDisplay);

                    return cartItemDto;
                })
                .collect(Collectors.toList());
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

	public void removeFromCart(String sessionId, Long productId) {
		// Log the input parameters for debugging
		System.out.println("Session ID: " + sessionId + ", Product ID: " + productId);

		// Retrieve the cart by session ID and handle if not found
		Cart cart = cartRepository.findBySessionId(sessionId)
				.orElseThrow(() -> new RuntimeException("Cart not found for session: " + sessionId));

		// Log the cart items before removal
		System.out.println("Cart items before removal: " + cart.getItems());

		// Find the cart item to remove based on productId
		CartItem cartItem = findCartItem(cart, productId);

		// Log the cart item to be removed
		System.out.println("Cart item to remove: " + cartItem);

		// Remove the item from the cart
		cart.getItems().remove(cartItem);

		// Log the updated cart items
		System.out.println("Cart items after removal: " + cart.getItems());

		// If the cart is empty after removal, delete the cart, otherwise save the
		// changes
		if (cart.getItems().isEmpty()) {
			cartRepository.delete(cart);
			System.out.println("Cart was empty and deleted.");
		} else {
			cartRepository.save(cart);
			System.out.println("Cart updated after item removal.");
		}
	}

	// Other methods: remove item, clear cart, etc.
}
