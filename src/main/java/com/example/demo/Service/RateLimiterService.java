package com.example.demo.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.RateLimitEntryRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.RateLimitEntry;

@Service
public class RateLimiterService {

	private final RateLimitEntryRepository rateLimitEntryRepository;
	private static final long COOLDOWN_SECONDS = 60;

	public RateLimiterService(RateLimitEntryRepository rateLimitEntryRepository) {
		this.rateLimitEntryRepository = rateLimitEntryRepository;
	}

	@Autowired
	private UserRepository userRepository;

	public boolean isAllowed(String email, String ip) {
		LocalDateTime now = LocalDateTime.now();
		RateLimitEntry entry = rateLimitEntryRepository.findByEmailAndIpAddress(email, ip).orElseGet(() -> {
			RateLimitEntry newEntry = new RateLimitEntry();
			newEntry.setEmail(email);
			newEntry.setIpAddress(ip);
			newEntry.setLastRequestTime(LocalDateTime.MIN); // ancient past
			return newEntry;
		});

		if (entry.getLastRequestTime().plusSeconds(COOLDOWN_SECONDS).isBefore(now)) {
			entry.setLastRequestTime(now);
			rateLimitEntryRepository.save(entry);
			return true;
		}

		return false;
	}

	public long getRemainingSeconds(String email, String ip) {
		LocalDateTime now = LocalDateTime.now();
		return rateLimitEntryRepository.findByEmailAndIpAddress(email, ip).map(entry -> {
			long secondsLeft = java.time.Duration.between(now, entry.getLastRequestTime().plusSeconds(COOLDOWN_SECONDS))
					.getSeconds();
			return Math.max(0, secondsLeft);
		}).orElse(0L);
	}

}
