package com.example.demo.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.NotificationService;
import com.example.demo.model.Notification;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

	private final NotificationService notificationService;

	public AdminNotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping("/unseen")
	public ResponseEntity<List<Notification>> getUnseenNotifications() {
		return ResponseEntity.ok(notificationService.getUnseenNotifications());
	}

	@PostMapping("/{id}/seen")
	public ResponseEntity<?> markAsSeen(@PathVariable Long id) {
		notificationService.markAsSeen(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/seen")
	public ResponseEntity<?> markAllAsSeen() {
		notificationService.markAllAsSeen();
		return ResponseEntity.ok().build();
	}
}
