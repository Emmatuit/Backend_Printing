package com.example.demo.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.WishlistItemDto;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.WishlistService;
import com.example.demo.model.UserEntity;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    public WishlistController(WishlistService wishlistService, UserRepository userRepository) {
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addToWishlist(@PathVariable("productId") Long productId,
                                           @RequestParam(required = false, name = "sessionId") String sessionId,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        String email = (userDetails != null) ? userDetails.getUsername() : null;

        if (email != null) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }
        }

        wishlistService.addToWishlist(email, sessionId, productId);
        return ResponseEntity.ok(Map.of("message", "Product added to wishlist"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable("productId") Long productId,
                                                @RequestParam(required = false, name = "sessionId") String sessionId,
                                                @AuthenticationPrincipal UserDetails userDetails) {

        String email = (userDetails != null) ? userDetails.getUsername() : null;

        if (email != null) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }
        }

        wishlistService.removeFromWishlist(email, sessionId, productId);
        return ResponseEntity.ok(Map.of("message", "Product removed from wishlist"));
    }

    @GetMapping
    public ResponseEntity<?> getWishlist(@RequestParam(required = false, name = "sessionId") String sessionId,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        String email = (userDetails != null) ? userDetails.getUsername() : null;

        if (email != null) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }
        }

        List<WishlistItemDto> wishlist = wishlistService.getWishlist(email, sessionId);
        return ResponseEntity.ok(wishlist);
    }
}
