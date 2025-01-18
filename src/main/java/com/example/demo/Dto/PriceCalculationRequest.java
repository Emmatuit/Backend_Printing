package com.example.demo.Dto;

import java.util.List;

public class PriceCalculationRequest {

	private long productId;
	private int selectedQuantity;
	private List<Long> selectedSpecOptionIds; // List of selected spec options IDs

	public long getProductId() {
		return productId;
	}

	public int getSelectedQuantity() {
		return selectedQuantity;
	}

	public List<Long> getSelectedSpecOptionIds() {
		return selectedSpecOptionIds;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public void setSelectedQuantity(int selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}

	public void setSelectedSpecOptionIds(List<Long> selectedSpecOptionIds) {
		this.selectedSpecOptionIds = selectedSpecOptionIds;
	}

}
