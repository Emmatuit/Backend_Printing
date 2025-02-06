package com.example.demo.Dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryDto {
	private Long id;
	private String name;
	private String description;
	private String encryptedImage; // To represent the image path or encryption
	 private List<SubcategoryDto> subcategories = new ArrayList<>(); // List of subcategories

	public CategoryDto() {
		// TODO Auto-generated constructor stub
	}

	public CategoryDto(Long id, String name, String description, String encryptedImage,
			List<SubcategoryDto> subcategories) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.encryptedImage = encryptedImage;
		this.subcategories = subcategories;
	}

	public String getDescription() {
		return description;
	}

	public String getEncryptedImage() {
		return encryptedImage;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<SubcategoryDto> getSubcategories() {
		return subcategories;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEncryptedImage(String encryptedImage) {
		this.encryptedImage = encryptedImage;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubcategories(List<SubcategoryDto> subcategories) {
		this.subcategories = subcategories;
	}

	// Getters and Setters
}