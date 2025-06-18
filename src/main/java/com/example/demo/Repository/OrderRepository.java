package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Order;
import com.example.demo.model.UserEntity;

public interface OrderRepository extends JpaRepository<Order, Long> {
	// Count how many orders this user has placed
    long countByUser(UserEntity user);

	Optional<Order> findByIdAndUser(Long id, UserEntity user);

    // Alternatively, if you want to use user id:
    // @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    // long countByUserId(@Param("userId") Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByTxRef(String txRef);
    List<Order> findByUser(UserEntity user);

    List<Order> findByUserOrderByCreatedAtDesc(UserEntity user);


}
