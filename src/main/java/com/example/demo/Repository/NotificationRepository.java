package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findBySeenFalse();
}
