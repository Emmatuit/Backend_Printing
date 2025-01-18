package com.example.demo.Dto;

import java.util.List;

public class CategorySubcategoryProductDto {
	private Long categoryId;
	private String categoryName;
	private List<SubcategoryDto> subcategories;

	public CategorySubcategoryProductDto() {

	}

	// Constructor, getters and setters
	public CategorySubcategoryProductDto(Long categoryId, String categoryName, List<SubcategoryDto> subcategories) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.subcategories = subcategories;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public List<SubcategoryDto> getSubcategories() {
		return subcategories;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setSubcategories(List<SubcategoryDto> subcategories) {
		this.subcategories = subcategories;
	}

	// Getters and setters...

}
