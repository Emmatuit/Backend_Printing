package com.example.demo.Dto;

public class DesignRequestDto {

	private Long productId; // Reference to the Product's ID
	private String designFileUrl;
	private String customerNotes;

	// Constructors, getters, and setters
	public DesignRequestDto() {
	}

	public DesignRequestDto(Long productId, String designFileUrl, String customerNotes) {
		this.productId = productId;
		this.designFileUrl = designFileUrl;
		this.customerNotes = customerNotes;
	}

	public String getCustomerNotes() {
		return customerNotes;
	}

	public String getDesignFileUrl() {
		return designFileUrl;
	}

	public Long getProductId() {
		return productId;
	}

	public void setCustomerNotes(String customerNotes) {
		this.customerNotes = customerNotes;
	}

	public void setDesignFileUrl(String designFileUrl) {
		this.designFileUrl = designFileUrl;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	// Getters and Setters...

}
