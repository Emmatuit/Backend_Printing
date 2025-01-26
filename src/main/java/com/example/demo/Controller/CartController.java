package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.CartDto;
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
	@PostMapping("/add")
	public ResponseEntity<String> addToCart(@RequestParam("sessionId") String sessionId, @RequestBody CartItemDto cartItemDTO) {
		cartService.addToCart(sessionId, cartItemDTO);
		return ResponseEntity.ok("Item added to cart!");
	}

	// Get the cart details

	@GetMapping("/get")
	public ResponseEntity<CartDto> getCart(@RequestParam("sessionId") String sessionId) {
		CartDto cartDto = cartService.getCart(sessionId);
		return ResponseEntity.ok(cartDto);
	}

	@GetMapping("/count")
	public int getCartItemCount(@RequestParam String sessionId) {
		Cart cart = cartRepository.findBySessionId(sessionId).orElse(new Cart(sessionId));
		return cart.getItems().size(); // Count of distinct products
	}

	 @GetMapping("/total")
	    public ResponseEntity<Map<String, Object>> getCartTotal(@RequestParam("sessionId") String sessionId) {
	        try {
	            // Calculate the total price for the Cart associated with the sessionId
//	            Double totalPrice = productService.calculateTotalPriceForSession(sessionId);

	            // Fetch all CartItems for the session using CartItemService
	            List<CartItemDto> cartItems = cartService.getAllCartItems(sessionId);

	            // Prepare the response: Include cart items and total price
	            Map<String, Object> response = new HashMap<>();
	            response.put("items", cartItems); // CartItems data
//	            response.put("totalPrice", totalPrice); // Total price for the session

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	@DeleteMapping("/remove")
	public ResponseEntity<String> removeFromCart(@RequestParam("sessionId") String sessionId, @RequestParam("productId") Long productId) {
		try {
			Cart cart = cartRepository.findBySessionId(sessionId)
					.orElseThrow(() -> new RuntimeException("Cart not found"));

			if (cart.getItems().isEmpty()) {
				// Handle empty cart scenario
				return ResponseEntity.status(HttpStatus.OK).body("Cart is empty, no items to remove.");
			}

			// Proceed to remove the item if the cart is not empty
			CartItem cartItem = cart.getItems().stream().filter(item -> item.getProductId().equals(productId))
					.findFirst().orElseThrow(() -> new IllegalArgumentException("Item not found in the cart"));

			cart.getItems().remove(cartItem);

			if (cart.getItems().isEmpty()) {
				cartRepository.delete(cart); // If the cart is empty, delete it
				return ResponseEntity.ok("Item removed and cart is now empty.");
			} else {
				cartRepository.save(cart); // If cart is not empty, save changes
				return ResponseEntity.ok("Item removed from cart.");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product ID or cart data.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found or error occurred.");
		}
	}

	@GetMapping("/items")
	public ResponseEntity<List<CartItemDto>> getAllCartItems(@RequestParam("sessionId") String sessionId) {
	    try {
	        List<CartItemDto> cartItems = cartService.getAllCartItems(sessionId);

	        // Check if the cart is empty
	        if (cartItems == null || cartItems.isEmpty()) {
	            return ResponseEntity.ok(new ArrayList<>()); // Return an empty list
	        }

	        return ResponseEntity.ok(cartItems); // Return 200 OK with the cart items
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(null); // Return 400 Bad Request for invalid input
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Return 500 for server errors
	    }
	}

}
