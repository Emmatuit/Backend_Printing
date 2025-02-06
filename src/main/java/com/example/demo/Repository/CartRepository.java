package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findBySessionId(String sessionId);
	
	@Query("SELECT COUNT(c) FROM CartItem c WHERE c.cart.sessionId = :sessionId")
	int countCartItems(@Param("sessionId") String sessionId);


}
