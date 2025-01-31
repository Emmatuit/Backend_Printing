package com.example.demo.Dto;

public class SpecificationOptionDTO {

	private Long id;
	private String name; // e.g., "Red", "Blue", "Small", "Large"
	private String image; // Image URL or path
	private Double price; // Additional price for this option

	public SpecificationOptionDTO() {
	}

	public SpecificationOptionDTO(Long id, String name, Double price, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.price = price;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public String getName() {
		return name;
	}

	public Double getPrice() {
		return price;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
