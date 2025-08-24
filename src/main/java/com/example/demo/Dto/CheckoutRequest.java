package com.example.demo.Dto;

import com.example.demo.model.Order.ShippingMethod;

public class CheckoutRequest {
	private String fullName;
	private String email;
	private String phoneNumber;
	private String address1;
	private String address2;
	private String state;
	private String postalCode;
	private String paymentMethod;
	private ShippingMethod shippingMethod; // ✅ New field added
	private String couponCode;

	public CheckoutRequest() {
		// Default constructor
	}

	public CheckoutRequest(String fullName, String email, String phoneNumber, String address1, String address2,
			String state, String postalCode, String paymentMethod, ShippingMethod shippingMethod, String couponCode) {
		super();
		this.fullName = fullName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address1 = address1;
		this.address2 = address2;
		this.state = state;
		this.postalCode = postalCode;
		this.paymentMethod = paymentMethod;
		this.shippingMethod = shippingMethod;
		this.couponCode = couponCode;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	public String getState() {
		return state;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public void setState(String state) {
		this.state = state;
	}
}
