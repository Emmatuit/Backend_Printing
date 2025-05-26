package com.example.demo.model;

import java.time.LocalDateTime;

import com.example.demo.Enum.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")

public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password; // Stored in **BCrypt hashed format**

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role; // ADMIN or USER

	private String emailVerificationCode; // store the 6-digit code

	private boolean isEmailVerified; // flag if verified

	private LocalDateTime verificationCodeExpiry; // optional expiry time for code

	private String resetPasswordToken; // token sent by email

	private LocalDateTime resetTokenExpiry; // token expiry time

	public UserEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserEntity(Long id, String username, String password, String email, String phoneNumber, Role role) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public String getEmailVerificationCode() {
		return emailVerificationCode;
	}

	public void setEmailVerificationCode(String emailVerificationCode) {
		this.emailVerificationCode = emailVerificationCode;
	}

	public boolean isEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public LocalDateTime getVerificationCodeExpiry() {
		return verificationCodeExpiry;
	}

	public void setVerificationCodeExpiry(LocalDateTime verificationCodeExpiry) {
		this.verificationCodeExpiry = verificationCodeExpiry;
	}

	public Long getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Role getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public LocalDateTime getResetTokenExpiry() {
		return resetTokenExpiry;
	}

	public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
		this.resetTokenExpiry = resetTokenExpiry;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", phoneNumber=" + phoneNumber + ", role=" + role + "]";
	}

	// Additional fields if needed

}
