package com.example.demo.Dto;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryDto {
	private Long id;
	private String name;
	private List<ProductDto> products = new ArrayList<>();
	private long categoryId;// List of products under this subcategory

	public SubcategoryDto() {

	}

	public SubcategoryDto(Long id, String name, List<ProductDto> products, long categoryId) {

		this.id = id;
		this.name = name;
		this.products = products;
		this.categoryId = categoryId;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ProductDto> getProducts() {
		return products;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProducts(List<ProductDto> products) {
		this.products = products;
	}

	// Getters and Setters
}