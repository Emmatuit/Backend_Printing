package com.example.demo.Dto;

import java.util.List;

public class CartDto {
	private Long id;
	private String sessionId;
	private List<CartItemDto> items;

	public CartDto() {
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
}
