package com.example.demo.Dto;

import org.springframework.beans.factory.annotation.Autowired;

public class OrderRequestDTO {

	@Autowired
	private ShippingdetailsDto shippingDetails;

	public ShippingdetailsDto getShippingDetails() {
		return shippingDetails;
	}

	public void setShippingDetails(ShippingdetailsDto shippingDetails) {
		this.shippingDetails = shippingDetails;
	}

}
