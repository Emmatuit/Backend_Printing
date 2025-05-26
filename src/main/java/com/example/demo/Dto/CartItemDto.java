package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;


public class CartItemDto {
	private ProductDto product;
	private int selectedQuantity;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal totalPrice; // Changed from Double to BigDecimal

	private List<SpecificationOptionDTO> selectedOptions = new ArrayList<>();
	
	private LocalDateTime addedAt;
    private LocalDateTime updatedAt;

    public CartItemDto() {
        this.addedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


	public CartItemDto(ProductDto product, int selectedQuantity, BigDecimal totalPrice,
			List<SpecificationOptionDTO> selectedOptions) {
		this.product = product;
		this.selectedQuantity = selectedQuantity;
		this.totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);
		this.selectedOptions = selectedOptions != null ? selectedOptions : new ArrayList<>();
	}

	
	
	
	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}


	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}


	// Getters and Setters
	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

	public int getSelectedQuantity() {
		return selectedQuantity;
	}

	public void setSelectedQuantity(int selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice != null ? totalPrice.setScale(2, RoundingMode.HALF_UP) : null;
	}

	public List<SpecificationOptionDTO> getSelectedOptions() {
		return selectedOptions;
	}

	public void setSelectedOptions(List<SpecificationOptionDTO> selectedOptions) {
		this.selectedOptions = selectedOptions != null ? selectedOptions : new ArrayList<>();
	}
}
