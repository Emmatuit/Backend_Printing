package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.CartItemDto;
import com.example.demo.Imagekit.ImagekitService;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.CartService;
import com.example.demo.Service.ProductService;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.UserEntity;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private ImagekitService imagekitService;
	
	@Autowired
	private UserRepository userRepository;

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
	 public ResponseEntity<List<CartItemDto>> getAllCartItems(
	         @RequestParam(value = "sessionId", required = false) String sessionId,
	         @RequestParam(value = "username", required = false) String username) {

	     List<CartItemDto> cartItems;

	     if (username != null) {
	         // ✅ If user is logged in, fetch cart by userId
	         UserEntity user = userRepository.findByUsername(username)
	                 .orElseThrow(() -> new RuntimeException("User not found"));
	         cartItems = cartService.getAllCartItemsByUser(user);
	     } else if (sessionId != null) {
	         // ✅ If user is not logged in, fetch cart by sessionId
	         cartItems = cartService.getAllCartItems(sessionId);
	     } else {
	         return ResponseEntity.badRequest().build();
	     }

	     return ResponseEntity.ok(cartItems);
	 }


	 @GetMapping("/count")
	 public ResponseEntity<Integer> getCartItemCount(
	         @RequestParam(value = "sessionId", required = false) String sessionId,
	         @AuthenticationPrincipal UserDetails userDetails) {

	     try {
	         int itemCount = 0;

	         if (userDetails != null) {
	             // 🔐 Logged-in user — get cart by UserEntity
	             UserEntity user = userRepository.findByUsername(userDetails.getUsername())
	                     .orElseThrow();

	             Optional<Cart> userCart = cartRepository.findByUser(user);
	             itemCount = userCart.map(cart -> cart.getItems().size()).orElse(0);

	         } else if (sessionId != null) {
	             // 🧑‍🚀 Guest user — get cart by sessionId
	             Optional<Cart> guestCart = cartRepository.findBySessionId(sessionId);
	             itemCount = guestCart.map(cart -> cart.getItems().size()).orElse(0);
	         } else {
	             return ResponseEntity.badRequest().body(0);
	         }

	         return ResponseEntity.ok(itemCount);

	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(0);
	     }
	 }

	 
	 @DeleteMapping("/remove")
	 public ResponseEntity<String> removeFromCart(
	         @RequestParam("productId") Long productId,
	         @RequestParam(value = "sessionId", required = false) String sessionId,
	         @AuthenticationPrincipal UserDetails userDetails) {

	     try {
	         Optional<Cart> optionalCart;

	         if (userDetails != null) {
	             // 🔐 Logged-in user
	             UserEntity user = userRepository.findByUsername(userDetails.getUsername())
	                     .orElseThrow();
	             optionalCart = cartRepository.findByUser(user);
	         } else if (sessionId != null) {
	             // 🧑‍🚀 Guest user
	             optionalCart = cartRepository.findBySessionId(sessionId);
	         } else {
	             return ResponseEntity.badRequest().body("Session ID or user must be provided.");
	         }

	         if (optionalCart.isEmpty()) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found.");
	         }

	         Cart cart = optionalCart.get();

	         Optional<CartItem> optionalItem = cart.getItems().stream()
	                 .filter(item -> item.getProduct().getId().equals(productId))
	                 .findFirst();

	         if (optionalItem.isEmpty()) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item not found in cart.");
	         }

	         CartItem cartItem = optionalItem.get();

	         // Delete image from ImageKit if exists
	         String fileId = cartItem.getDesignRequest() != null ? cartItem.getDesignRequest().getFileId() : null;
	         if (fileId != null) {
	             boolean deleted = imagekitService.deleteFileFromImageKit(fileId);
	             if (!deleted) {
	                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image from ImageKit.");
	             }
	         }

	         // Remove item from cart
	         cart.getItems().remove(cartItem);

	         if (cart.getItems().isEmpty()) {
	             cartRepository.delete(cart);
	             return ResponseEntity.ok("Item removed. Cart deleted.");
	         } else {
	             cartRepository.save(cart);
	             return ResponseEntity.ok("Item removed successfully.");
	         }

	     } catch (Exception e) {
	         e.printStackTrace();
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the item.");
	     }
	 }

}
