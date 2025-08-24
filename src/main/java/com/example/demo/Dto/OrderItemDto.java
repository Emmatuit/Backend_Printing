package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderItemDto {
	private Long productId;
	private String productName;
	private int quantity;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal price; // Changed from double to BigDecimal

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal subTotal; // price * quantity

	private LocalDateTime createdAt;

	private List<SelectedSpecificationDto> selectedSpecifications;
	private DesignRequestDto designRequest;

	public OrderItemDto() {
		this.price = BigDecimal.ZERO;
		this.subTotal = BigDecimal.ZERO;
		this.createdAt = LocalDateTime.now();
	}

	public OrderItemDto(Long productId, String productName, int quantity, BigDecimal price) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public BigDecimal getPrice() {
		return price;
	}

	// Getters and Setters
	public Long getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
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

	public void setPrice(BigDecimal price) {
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public List<SelectedSpecificationDto> getSelectedSpecifications() {
		return selectedSpecifications;
	}

	public void setSelectedSpecifications(List<SelectedSpecificationDto> selectedSpecifications) {
		this.selectedSpecifications = selectedSpecifications;
	}

	public DesignRequestDto getDesignRequest() {
		return designRequest;
	}

	public void setDesignRequest(DesignRequestDto designRequest) {
		this.designRequest = designRequest;
	}

}
