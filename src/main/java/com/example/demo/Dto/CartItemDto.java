package com.example.demo.Dto;

import java.util.List;

public class CartItemDto {
	private ProductDto product;
	private int selectedQuantity;
	  private Double totalPrice;
	  
	private List<SpecificationOptionDTO> selectedOptions;
	
	
	// Getters and Setters
	public ProductDto getProduct() {
		return product;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<SpecificationOptionDTO> getSelectedOptions() {
		return selectedOptions;
	}

	public int getSelectedQuantity() {
		return selectedQuantity;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

	public void setSelectedOptions(List<SpecificationOptionDTO> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	public void setSelectedQuantity(int selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}
}
