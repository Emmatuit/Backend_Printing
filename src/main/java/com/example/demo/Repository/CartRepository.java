package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cart;
import com.example.demo.model.UserEntity;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	@Query("SELECT COUNT(c) FROM CartItem c WHERE c.cart.sessionId = :sessionId")
	int countCartItems(@Param("sessionId") String sessionId);

	Optional<Cart> findBySessionId(String sessionId);

	Optional<Cart> findByUser(UserEntity user);

	void deleteAllByUser(UserEntity user);

}
