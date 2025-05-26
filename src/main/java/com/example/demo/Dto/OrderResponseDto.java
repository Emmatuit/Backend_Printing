package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderResponseDto {
    private Long id;
    private String username;
    private List<OrderItemDto> items;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalAmount;  // Changed from double to BigDecimal
    
    private String shippingAddress;
    private String status;
    private LocalDateTime createdAt;

    public OrderResponseDto() {
        this.totalAmount = BigDecimal.ZERO;
    }

    public OrderResponseDto(Long id, String username, List<OrderItemDto> items, 
                          BigDecimal totalAmount, String shippingAddress, 
                          String status, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.items = items;
        this.totalAmount = totalAmount != null ? 
            totalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? 
            totalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
