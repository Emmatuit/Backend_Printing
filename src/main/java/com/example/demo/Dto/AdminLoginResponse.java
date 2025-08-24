package com.example.demo.Dto;

public class AdminLoginResponse {
	private String message;
	private String token;
	private String dashboardUrl;

	public AdminLoginResponse(String message, String token, String dashboardUrl) {
		this.message = message;
		this.token = token;
		this.dashboardUrl = dashboardUrl;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public AdminLoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
}
