package com.example.demo.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartDto {
	private Long id;
	private String sessionId;
	private List<CartItemDto> items;
	private BigDecimal cartTotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee;
    private BigDecimal grandTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

	public CartDto() {
		this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
	}

	public CartDto(Long id, String sessionId, List<CartItemDto> items) {
		this.id = id;
		this.sessionId = sessionId;
		this.items = items;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public List<CartItemDto> getItems() {
		return items;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItems(List<CartItemDto> items) {
		this.items = items;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public BigDecimal getCartTotal() {
		return cartTotal;
	}

	public void setCartTotal(BigDecimal cartTotal) {
		this.cartTotal = cartTotal;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(BigDecimal shippingFee) {
		this.shippingFee = shippingFee;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
}
