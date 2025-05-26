package com.example.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class DesignRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileName;
	private String fileType;
	private String fileUrl; // URL of the uploaded file (either cloud URL or local path)
	private String description; // Add description field for the image
	private String fileId; // ImageKit file ID for deletion

	@OneToOne(mappedBy = "designRequest", cascade = CascadeType.ALL)
	private CartItem cartItem; // Linked to the cart item

	public DesignRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DesignRequest(Long id, String fileName, String fileType, String fileUrl, String description, String fileId,
			CartItem cartItem) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.description = description;
		this.fileId = fileId;
		this.cartItem = cartItem;
	}

	public CartItem getCartItem() {
		return cartItem;
	}

	public String getDescription() {
		return description;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public Long getId() {
		return id;
	}

	public void setCartItem(CartItem cartItem) {
		this.cartItem = cartItem;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// Getters and setters
}