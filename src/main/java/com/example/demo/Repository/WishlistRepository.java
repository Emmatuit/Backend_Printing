package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.WishlistItem;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

	List<WishlistItem> findByUserEmail(String email);

	Optional<WishlistItem> findByUserEmailAndProductId(String email, Long productId);

	void deleteByUserEmailAndProductId(String email, Long productId);

	List<WishlistItem> findBySessionId(String sessionId);

	Optional<WishlistItem> findBySessionIdAndProductId(String sessionId, Long productId);

	void deleteBySessionIdAndProductId(String sessionId, Long productId);
}
