package com.example.demo.Dto;

import java.util.List;

public class CartItemDto {
	private ProductDto product;
	private int selectedQuantity;
	private Double totalPrice;
	private List<SpecificationOptionDTO> selectedOptions;




	public CartItemDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CartItemDto(ProductDto product, int selectedQuantity, Double totalPrice,
			List<SpecificationOptionDTO> selectedOptions) {
		super();
		this.product = product;
		this.selectedQuantity = selectedQuantity;
		this.totalPrice = totalPrice;
		this.selectedOptions = selectedOptions;

	}



	// Getters and Setters
	public ProductDto getProduct() {
		return product;
	}

	public List<SpecificationOptionDTO> getSelectedOptions() {
		return selectedOptions;
	}

	public int getSelectedQuantity() {
		return selectedQuantity;
	}

	public Double getTotalPrice() {
		return totalPrice;
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

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
}
