package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.RateLimitEntry;

public interface RateLimitEntryRepository extends JpaRepository<RateLimitEntry, Long> {
    Optional<RateLimitEntry> findByEmailAndIpAddress(String email, String ipAddress);
}
