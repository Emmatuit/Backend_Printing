package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // Unique identifier for the cart

	@Column(nullable = true)
	private String sessionId; // Session identifier for non-logged-in users

	@Column(nullable = true)
	private Long userId; // User identifier for logged-in users

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "cart", orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>(); // List of cart items

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt; // Timestamp for when the cart was created

	// Constructors, getters, and setters
	public Cart() {
	}

	public Cart(Long userId) {
		this.userId = userId;
	}

	public Cart(String sessionId) {
		this.sessionId = sessionId;
	}

	// Add a cart item
	public void addItem(CartItem item) {
		item.setCart(this);
		this.items.add(item);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public Long getId() {
		return id;
	}

	public List<CartItem> getItems() {
		return items;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Long getUserId() {
		return userId;
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	// Remove a cart item
	public void removeItem(CartItem item) {
		this.items.remove(item);
		item.setCart(null);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
