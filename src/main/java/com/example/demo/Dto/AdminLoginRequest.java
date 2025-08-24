package com.example.demo.Dto;

public class AdminLoginRequest {
	private String email;
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AdminLoginRequest(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public AdminLoginRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
}
