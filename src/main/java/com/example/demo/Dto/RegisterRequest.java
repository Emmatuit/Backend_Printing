package com.example.demo.Dto;

public class RegisterRequest {

	private String username;

	private String password;

	private String email;

	private String phoneNumber;

	private String address;

	public String getAddress() {
		return address;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
