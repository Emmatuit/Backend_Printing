package com.example.demo.Controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dto.CartItemDto;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Service.CartService;
import com.example.demo.Service.ProductService;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductService productService;

	// Add item to cart
//	@PostMapping("/add")
//	public ResponseEntity<String> addToCart(@RequestParam("sessionId") String sessionId,
//			@RequestBody CartItemDto cartItemDTO) {
//		if (cartItemDTO.getProduct() == null || cartItemDTO.getSelectedOptions() == null) {
//			return ResponseEntity.badRequest().body("Product or selected options cannot be null.");
//		}
//		cartService.addItemToCart(sessionId, cartItemDTO);
//		return ResponseEntity.ok("Item added to cart!");
//	}
	
	 @PostMapping("/add")
	    public ResponseEntity<String> addItemToCart(
	            @RequestParam ("sessionId") String sessionId, 
	            @RequestParam ("designRequestId") Long designRequestId, 
	            @RequestBody CartItemDto cartItemDto) {

			if (cartItemDto.getProduct() == null || cartItemDto.getSelectedOptions() == null) {
				return ResponseEntity.badRequest().body("Product or selected options cannot be null.");
			}
	        cartService.addItemToCart(sessionId, cartItemDto, designRequestId);
	        return ResponseEntity.ok("Item added to cart!");
	    }
	

	// Get the cart details

	@GetMapping("/items")
	public ResponseEntity<List<CartItemDto>> getAllCartItems(@RequestParam("sessionId") String sessionId) {
	    // Get all cart items for the given session ID
	    List<CartItemDto> cartItems = cartService.getAllCartItems(sessionId);

	    // Return empty list if cart is empty
	    return ResponseEntity.ok(cartItems);
	}


	@GetMapping("/cart/count")
	public ResponseEntity<Integer> getCartItemCount(@RequestParam("sessionId") String sessionId) {
	    int itemCount = cartService.getCartItemCount(sessionId);
	    return ResponseEntity.ok(itemCount);
	}


	//	@GetMapping("/total")
//	public ResponseEntity<Map<String, Object>> getCartTotal(@RequestParam("sessionId") String sessionId) {
//		try {
//			// Calculate the total price for the Cart associated with the sessionId
////	            Double totalPrice = productService.calculateTotalPriceForSession(sessionId);
//
//			// Fetch all CartItems for the session using CartItemService
//			List<CartItemDto> cartItems = cartService.getAllCartItems(sessionId);
//
//			// Prepare the response: Include cart items and total price
//			Map<String, Object> response = new HashMap<>();
//			response.put("items", cartItems); // CartItems data
////	            response.put("totalPrice", totalPrice); // Total price for the session
//
//			return ResponseEntity.ok(response);
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//		}
//	}
//
	@DeleteMapping("/remove")
	public ResponseEntity<String> removeFromCart(@RequestParam("sessionId") String sessionId,
			@RequestParam("productId") Long productId) {
		try {
			// Fetch the cart associated with the session
			Cart cart = cartRepository.findBySessionId(sessionId)
					.orElseThrow(() -> new RuntimeException("Cart not found"));

			// Check if the cart has any items
			if (cart.getItems().isEmpty()) {
				return ResponseEntity.status(HttpStatus.OK).body("Cart is empty, no items to remove.");
			}

			// Try to find the cart item by productId
			CartItem cartItem = cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId)) // Compare
																														// product
																														// IDs
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Item not found in the cart"));

			// Remove the cart item
			cart.getItems().remove(cartItem);

			// Check if the cart is now empty and delete it if so
			if (cart.getItems().isEmpty()) {
				cartRepository.delete(cart); // Delete the cart if it's empty
				return ResponseEntity.ok("Item removed and cart is now empty.");
			} else {
				cartRepository.save(cart); // Save the cart after removing the item
				return ResponseEntity.ok("Item removed from cart.");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Invalid product ID or item not found in the cart.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found or error occurred.");
		}
	}

}
