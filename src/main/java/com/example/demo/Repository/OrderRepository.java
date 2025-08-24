package com.example.demo.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Enum.OrderStatus;
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

	List<Order> findByUserAndStatusOrderByCreatedAtDesc(UserEntity user, OrderStatus status);

	List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

	// For pagination
	Page<Order> findByStatus(OrderStatus status, Pageable pageable);

	// For search
	List<Order> findByEmailContainingIgnoreCase(String email);

	List<Order> findByEmailContainingIgnoreCaseAndOrderNumber(String email, String orderNumber);

	@Query("""
			    SELECT o FROM Order o
			    WHERE (:status IS NULL OR o.paymentStatus = :status)
			      AND (:method IS NULL OR o.paymentMethod = :method)
			      AND (:orderNumber IS NULL OR o.orderNumber = :orderNumber)
			      AND (:startDate IS NULL OR o.createdAt >= :startDate)
			      AND (:endDate IS NULL OR o.createdAt <= :endDate)
			""")
	Page<Order> findPaymentHistory(@Param("status") String status, @Param("method") String method,
			@Param("orderNumber") String orderNumber, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, Pageable pageable);

}
