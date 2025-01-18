package com.example.demo.Controller;

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
	public ResponseEntity<String> addToCart(@RequestParam String sessionId, @RequestBody CartItemDto cartItemDTO) {
		cartService.addToCart(sessionId, cartItemDTO);
		return ResponseEntity.ok("Item added to cart!");
	}

	// Get the cart details

	@GetMapping("/get")
	public ResponseEntity<CartDto> getCart(@RequestParam String sessionId) {
		CartDto cartDto = cartService.getCart(sessionId);
		return ResponseEntity.ok(cartDto);
	}

	@GetMapping("/count")
	public int getCartItemCount(@RequestParam String sessionId) {
		Cart cart = cartRepository.findBySessionId(sessionId).orElse(new Cart(sessionId));
		return cart.getItems().size(); // Count of distinct products
	}

	// Endpoint to calculate and retrieve the total price of the cart
	@GetMapping("/CartTotal")
	public ResponseEntity<Double> getCartTotalPrice(@RequestParam String sessionId) {
		Double totalPrice = productService.calculateCartTotalPrice(sessionId);
		return ResponseEntity.ok(totalPrice);
	}

	@DeleteMapping("/remove")
	public ResponseEntity<String> removeFromCart(@RequestParam String sessionId, @RequestParam Long productId) {
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

}
