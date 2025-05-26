package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
@Table(name = "cart_items")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private int selectedQuantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart; // Ensuring cart_id is always required for cart items

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal totalPrice; // Changed from double to BigDecimal

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "cart_item_id")
	private List<SpecificationOption> selectedOptions;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "designRequest_id", referencedColumnName = "id")
	private DesignRequest designRequest;

	public CartItem() {
	}

	public CartItem(Product product, int selectedQuantity, BigDecimal totalPrice,
			List<SpecificationOption> selectedOptions) {
		this.product = product;
		this.selectedQuantity = selectedQuantity;
		this.totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);
		this.selectedOptions = selectedOptions != null ? selectedOptions : new ArrayList<>();
	}

	// Backward compatibility for double
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
	}

	@Transient
	public double getTotalPriceAsDouble() {
		return totalPrice != null ? totalPrice.doubleValue() : 0.0;
	}

	// Getters and Setters (other methods remain the same)
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice != null ? totalPrice.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	public Cart getCart() {
		return cart;
	}

	public DesignRequest getDesignRequest() {
		return designRequest;
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public List<SpecificationOption> getSelectedOptions() {
		return selectedOptions;
	}

	public int getSelectedQuantity() {
		return selectedQuantity;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public void setDesignRequest(DesignRequest designRequest) {
		this.designRequest = designRequest;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setSelectedOptions(List<SpecificationOption> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	public void setSelectedQuantity(int selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}

}
