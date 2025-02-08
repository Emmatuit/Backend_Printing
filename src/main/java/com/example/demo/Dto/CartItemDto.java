package com.example.demo.Dto;

import java.util.List;

public class CartItemDto {
	private ProductDto product;
	private int selectedQuantity;
	  private Double totalPrice;
	private List<SpecificationOptionDTO> selectedOptions;
	 private DesignRequestDto designRequestDto;
	
		 
	 
	public CartItemDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CartItemDto(ProductDto product, int selectedQuantity, Double totalPrice,
			List<SpecificationOptionDTO> selectedOptions, DesignRequestDto designRequestDto) {
		super();
		this.product = product;
		this.selectedQuantity = selectedQuantity;
		this.totalPrice = totalPrice;
		this.selectedOptions = selectedOptions;
		this.designRequestDto = designRequestDto;
	}

	public DesignRequestDto getDesignRequestDto() {
		return designRequestDto;
	}

	public void setDesignRequestDto(DesignRequestDto designRequestDto) {
		this.designRequestDto = designRequestDto;
	}

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
