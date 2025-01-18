package com.example.demo.Dto;

public class CartItemDto {
	private Long productId;
	private Integer selectedQuantity;
	private CartProductDisplay productDisplay; // Store CartProductDisplay here
	private Double calculatedPrice; // New field for the calculated price

	public CartItemDto() {
	}

	// Constructor
	public CartItemDto(Long productId, Integer selectedQuantity, CartProductDisplay productDisplay,
			Double calculatedPrice) {
		this.productId = productId;
		this.selectedQuantity = selectedQuantity;
		this.productDisplay = productDisplay;
		this.calculatedPrice = calculatedPrice;
	}

	public Double getCalculatedPrice() {
		return calculatedPrice;
	}

	public CartProductDisplay getProductDisplay() {
		return productDisplay;
	}

	// Getters and setters
	public Long getProductId() {
		return productId;
	}

	public Integer getSelectedQuantity() {
		return selectedQuantity;
	}

	public void setCalculatedPrice(Double calculatedPrice) {
		this.calculatedPrice = calculatedPrice;
	}

	public void setProductDisplay(CartProductDisplay productDisplay) {
		this.productDisplay = productDisplay;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setSelectedQuantity(Integer selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}
}
