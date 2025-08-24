package com.example.demo.Dto;

import java.time.LocalDateTime;

import com.example.demo.Enum.Role;

public class UserDto {
	private Long id;
	private String username;
	private String email;
	private String phoneNumber;
	private Role role;
	private boolean isEmailVerified;
	private LocalDateTime createdAt;

	public UserDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserDto(Long id, String username, String email, String phoneNumber, Role role, boolean isEmailVerified,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.isEmailVerified = isEmailVerified;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// You can also add Getters if you're using a JSON serializer like Jackson
}
