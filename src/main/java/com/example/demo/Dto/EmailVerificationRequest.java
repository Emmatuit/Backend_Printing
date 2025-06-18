package com.example.demo.Dto;

public class EmailVerificationRequest {

	private String email;
	private String code; // 6-digit string

	public EmailVerificationRequest() {
	} // default ctor (needed by Spring)

	public EmailVerificationRequest(String email, String code) {
		this.email = email;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getEmail() {
		return email;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
