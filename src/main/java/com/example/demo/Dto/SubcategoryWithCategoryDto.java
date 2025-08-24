package com.example.demo.Dto;

public class SubcategoryWithCategoryDto {
	private Long id;
	private String name;
	private Long categoryId;
	private String categoryName;
	private long productCount; // 👈 New field

	public SubcategoryWithCategoryDto() {
	}

	public SubcategoryWithCategoryDto(Long id, String name, Long categoryId, String categoryName, long productCount) {
		this.id = id;
		this.name = name;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.productCount = productCount;
	}

	// Getters and Setters (include productCount)
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public long getProductCount() {
		return productCount;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setProductCount(long productCount) {
		this.productCount = productCount;
	}
}
