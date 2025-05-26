package com.example.demo.Features;

import java.time.LocalDateTime;

import com.example.demo.model.Product;
import com.example.demo.model.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ClickedProductHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Product product;

	@ManyToOne
	private UserEntity user;

	private String sessionId;

	private LocalDateTime clickedAt;

	public ClickedProductHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClickedProductHistory(Long id, Product product, UserEntity user, String sessionId, LocalDateTime clickedAt) {
		super();
		this.id = id;
		this.product = product;
		this.user = user;
		this.sessionId = sessionId;
		this.clickedAt = clickedAt;
	}

	public LocalDateTime getClickedAt() {
		return clickedAt;
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public String getSessionId() {
		return sessionId;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setClickedAt(LocalDateTime clickedAt) {
		this.clickedAt = clickedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

}
