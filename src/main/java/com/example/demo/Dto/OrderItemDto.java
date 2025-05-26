package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;  // Changed from double to BigDecimal
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal subTotal; // price * quantity
    
    private LocalDateTime createdAt;
    
    

    public OrderItemDto() {
    	this.price = BigDecimal.ZERO;
        this.subTotal = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? 
            price.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
    
    
}
