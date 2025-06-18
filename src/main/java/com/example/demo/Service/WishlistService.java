package com.example.demo.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.WishlistItemDto;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.WishlistRepository;
import com.example.demo.model.Product;
import com.example.demo.model.UserEntity;
import com.example.demo.model.WishlistItem;

import jakarta.transaction.Transactional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addToWishlist(String email, String sessionId, Long productId) {
        Optional<WishlistItem> existingItem = email != null
                ? wishlistRepository.findByUserEmailAndProductId(email, productId)
                : wishlistRepository.findBySessionIdAndProductId(sessionId, productId);

        if (existingItem.isPresent()) {
            throw new RuntimeException("Product is already in wishlist");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WishlistItem item = new WishlistItem();
        item.setProduct(product);

        if (email != null) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            item.setUser(user);
            item.setSessionId(null);
        } else {
            item.setUser(null);
            item.setSessionId(sessionId);
        }

        wishlistRepository.save(item);
    }

    @Transactional
    public void removeFromWishlist(String email, String sessionId, Long productId) {
        if (email != null) {
            wishlistRepository.deleteByUserEmailAndProductId(email, productId);
        } else {
            wishlistRepository.deleteBySessionIdAndProductId(sessionId, productId);
        }
    }

    public List<WishlistItemDto> getWishlist(String email, String sessionId) {
        List<WishlistItem> items = email != null
                ? wishlistRepository.findByUserEmail(email)
                : wishlistRepository.findBySessionId(sessionId);

        return items.stream().map(item -> {
            WishlistItemDto dto = new WishlistItemDto();
            dto.setId(item.getId());
            dto.setAddedAt(item.getAddedAt());
            dto.setProduct(mapToDto(item.getProduct()));
            return dto;
        }).collect(Collectors.toList());
    }


    private ProductDto mapToDto(Product product) {
		ProductDto dto = new ProductDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setBasePrice(product.getBaseprice());
		dto.setMinOrderQuantity(product.getMinOrderquantity());
		dto.setMaxQuantity(product.getMaxQuantity());
		dto.setIncrementStep(product.getIncrementStep());
		dto.setEncryptedImages(product.getEncryptedImages());
		dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
		dto.setSubcategoryId(product.getSubcategory() != null ? product.getSubcategory().getId() : null);

		// ✅ Add these two lines
		dto.setViews(product.getViews());
		dto.setCreatedAt(product.getCreatedAt());

		return dto;
	}


    @Transactional
    public void mergeSessionWishlistWithUser(String email, String sessionId) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with Email not found"));

        System.out.println("🔹 Merging wishlist for user: " + email);
        System.out.println("🔹 Provided sessionId: " + sessionId);

        if (sessionId == null) {
            System.out.println("❌ No session ID provided, skipping merge.");
            return;
        }

        List<WishlistItem> sessionWishlistItems = wishlistRepository.findBySessionId(sessionId);

        if (sessionWishlistItems.isEmpty()) {
            System.out.println("❌ No wishlist items found for sessionId: " + sessionId);
            return;
        }

        System.out.println("✅ Found " + sessionWishlistItems.size() + " wishlist items for sessionId: " + sessionId);

        List<WishlistItem> userWishlistItems = wishlistRepository.findByUserEmail(email);

        Set<Long> userProductIds = userWishlistItems.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());

        for (WishlistItem item : sessionWishlistItems) {
            if (!userProductIds.contains(item.getProduct().getId())) {
                item.setUser(user);
                item.setSessionId(null);
                wishlistRepository.save(item);
                System.out.println("✅ Merged product ID " + item.getProduct().getId() + " to user wishlist");
            } else {
                wishlistRepository.delete(item);
                System.out.println("⚠️ Duplicate product ID " + item.getProduct().getId() + " skipped");
            }
        }

        System.out.println("✅ Wishlist merged successfully for user: " + email);
    }
}

