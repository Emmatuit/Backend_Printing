package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal price; // Changed from double to BigDecimal

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal subTotal;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "design_request_id", referencedColumnName = "id")
	private DesignRequest designRequest;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SelectedSpecification> selectedSpecifications = new ArrayList<>();

	public OrderItem() {
	}

	public OrderItem(Long id, Order order, Product product, int quantity, BigDecimal price) {
		this.id = id;
		this.order = order;
		this.product = product;
		this.quantity = quantity;
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public Order getOrder() {
		return order;
	}

	public BigDecimal getPrice() {
		return price;
	}

	// Backward compatibility for double (if needed)
	@Transient
	public double getPriceAsDouble() {
		return price != null ? price.doubleValue() : 0.0;
	}

	public Product getProduct() {
		return product;
	}

	public int getQuantity() {
		return quantity;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setPrice(BigDecimal price) {
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
	}

	@Transient
	public void setPriceAsDouble(double price) {
		this.price = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public List<SelectedSpecification> getSelectedSpecifications() {
		return selectedSpecifications;
	}

	public void setSelectedSpecifications(List<SelectedSpecification> selectedSpecifications) {
		this.selectedSpecifications = selectedSpecifications;
	}

	public DesignRequest getDesignRequest() {
		return designRequest;
	}

	public void setDesignRequest(DesignRequest designRequest) {
		this.designRequest = designRequest;
	}

}
