package com.example.demo.Dto;

import java.util.List;

public class CartDto {
	private String sessionId;
	private List<CartItemDto> items;

	public List<CartItemDto> getItems() {
		return items;
	}

	// Getters and setters
	public String getSessionId() {
		return sessionId;
	}

	public void setItems(List<CartItemDto> items) {
		this.items = items;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
