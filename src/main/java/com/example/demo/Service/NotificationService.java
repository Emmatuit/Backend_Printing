package com.example.demo.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.NotificationRepository;
import com.example.demo.model.Notification;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public NotificationService(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	public void createNotification(String title, String message, Notification.NotificationType type) {
		Notification notification = new Notification();
		notification.setTitle(title);
		notification.setMessage(message);
		notification.setType(type);
		notificationRepository.save(notification);
	}

	public List<Notification> getUnseenNotifications() {
		return notificationRepository.findBySeenFalse();
	}

	public void markAsSeen(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found"));
		notification.setSeen(true);
		notificationRepository.save(notification);
	}

	public void markAllAsSeen() {
		List<Notification> unseen = notificationRepository.findBySeenFalse();
		unseen.forEach(n -> n.setSeen(true));
		notificationRepository.saveAll(unseen);
	}
}
