package com.example.demo.Dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryDto {
	private Long id;
	private String name;
	private String description;
	private List<String> encryptedImages;// To represent the image path or encryption
	private List<SubcategoryDto> subcategories = new ArrayList<>(); // List of subcategories

	public CategoryDto() {
		// TODO Auto-generated constructor stub
	}

	public CategoryDto(Long id, String name, String description, List<String> encryptedImages,
			List<SubcategoryDto> subcategories) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.encryptedImages = encryptedImages;
		this.subcategories = subcategories;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getEncryptedImages() {
		return encryptedImages;
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

	public void setEncryptedImages(List<String> encryptedImages) {
		this.encryptedImages = encryptedImages;
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