package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Order;
import com.example.demo.model.UserEntity;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUser(UserEntity user);
}
