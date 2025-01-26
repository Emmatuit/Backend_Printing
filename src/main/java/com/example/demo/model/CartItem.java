package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id")
	private Cart cart; // Relationship to the Cart

	@Column(nullable = false)
	private Double price;


    @ElementCollection
    private List<Long> selectedOptionIds = new ArrayList<>(); // Store selected option IDs


	public List<Long> getSelectedOptionIds() {
		return selectedOptionIds;
	}

	public void setSelectedOptionIds(List<Long> selectedOptionIds) {
		this.selectedOptionIds = selectedOptionIds;
	}

	// Constructors, getters, and setters
	public CartItem() {
	}



	public CartItem(Long id, Long productId, Integer quantity, Cart cart, Double price, List<Long> selectedOptionIds) {
		super();
		this.id = id;
		this.productId = productId;
		this.quantity = quantity;
		this.cart = cart;
		this.price = price;
		this.selectedOptionIds = selectedOptionIds;
	}

	// Other getters and setters...

	public Cart getCart() {
		return cart;
	}

	public Long getId() {
		return id;
	}

	public Double getPrice() {
		return price;
	}

	public Long getProductId() {
		return productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
