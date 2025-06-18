package com.example.demo.Dto;

public class ResetPasswordRequest {
	private String token; // the 4-digit code

	private String newPassword;

	private String Email;

	public String getEmail() {
		return Email;
	}

	public String getNewPassword() {
		return newPassword;
	}

	// Getters and Setters
	public String getToken() {
		return token;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
