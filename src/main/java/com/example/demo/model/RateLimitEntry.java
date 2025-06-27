package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "rate_limit_entries", indexes = {
    @Index(columnList = "email, ipAddress", unique = true)
})
public class RateLimitEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String ipAddress;

    private LocalDateTime lastRequestTime;

    // Getters and setters
    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getLastRequestTime() { return lastRequestTime; }
    public void setLastRequestTime(LocalDateTime lastRequestTime) { this.lastRequestTime = lastRequestTime; }
}
