package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class WishlistItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private UserEntity user; // nullable, because guest user may not be logged in yet

	private String sessionId; // for guest users

	@ManyToOne(optional = false)
	private Product product;

	private LocalDateTime addedAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}

	public WishlistItem(Long id, UserEntity user, String sessionId, Product product, LocalDateTime addedAt) {
		super();
		this.id = id;
		this.user = user;
		this.sessionId = sessionId;
		this.product = product;
		this.addedAt = addedAt;
	}

	public WishlistItem() {
		super();
		// TODO Auto-generated constructor stub
	}

}
