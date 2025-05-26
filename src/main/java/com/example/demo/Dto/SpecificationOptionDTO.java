package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SpecificationOptionDTO {
	private Long id;
	private String name;
	private String image;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal price; // Changed from Double to BigDecimal

	// Constructors
	public SpecificationOptionDTO() {
	}

	public SpecificationOptionDTO(Long id, String name, BigDecimal price, String image) {
		this.id = id;
		this.name = name;
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
		this.image = image;
	}

	// Backward compatibility for Double
	@JsonIgnore
	public Double getPriceAsDouble() {
		return price != null ? price.doubleValue() : null;
	}

	@JsonIgnore
	public void setPriceAsDouble(Double price) {
		this.price = price != null ? BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) : null;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
	}
}