package com.example.demo.model;

import java.time.LocalDateTime;

import com.example.demo.Enum.DesignStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DesignRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product; // Reference to the Product entity

	@Column(nullable = false)
	private String designFileUrl; // URL to the uploaded design file

	@Enumerated(EnumType.STRING)
	private DesignStatus designStatus = DesignStatus.PENDING; // Default status is PENDING

	private String customerNotes; // Optional notes from the customer

	private LocalDateTime submittedAt = LocalDateTime.now(); // Timestamp for submission

	// Constructors, getters, and setters
	public DesignRequest() {
	}

	public DesignRequest(Product product, String designFileUrl, String customerNotes) {
		this.product = product;
		this.designFileUrl = designFileUrl;
		this.customerNotes = customerNotes;
	}

	public String getCustomerNotes() {
		return customerNotes;
	}

	public String getDesignFileUrl() {
		return designFileUrl;
	}

	public DesignStatus getDesignStatus() {
		return designStatus;
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setCustomerNotes(String customerNotes) {
		this.customerNotes = customerNotes;
	}

	public void setDesignFileUrl(String designFileUrl) {
		this.designFileUrl = designFileUrl;
	}

	public void setDesignStatus(DesignStatus designStatus) {
		this.designStatus = designStatus;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	// Getters and Setters...

}
