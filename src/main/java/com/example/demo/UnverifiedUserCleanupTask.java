package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.Repository.UserRepository;
import com.example.demo.model.UserEntity;

@Component
public class UnverifiedUserCleanupTask {

    @Autowired
    private UserRepository userRepository;

    // Runs once a day at midnight (00:00)
    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void deleteOldUnverifiedUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        List<UserEntity> expired = userRepository.findUnverifiedUsersBefore(threshold);
        userRepository.deleteAll(expired);
        System.out.println("Deleted " + expired.size() + " unverified users older than 7 days.");
    }
}
