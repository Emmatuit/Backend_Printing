package com.example.demo.Dto;

public class ShippingdetailsDto {
	private String fullName;
	private String email;
	private String phoneNumber;
	private String address1;
	private String address2;
	private String state;
	private String postalCode;

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
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

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setState(String state) {
		this.state = state;
	}

}
