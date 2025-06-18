package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.demo.model.ImageInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SpecificationOptionDTO {
	private Long id;
	private String name;
	private ImageInfo image;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal price; // Changed from Double to BigDecimal

	// Constructors
	public SpecificationOptionDTO() {
	}

	public SpecificationOptionDTO(Long id, String name, ImageInfo image, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}



	public ImageInfo getImage() {
		return image;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	// Backward compatibility for Double
	@JsonIgnore
	public Double getPriceAsDouble() {
		return price != null ? price.doubleValue() : null;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setImage(ImageInfo imageInfo) {
		this.image = imageInfo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(BigDecimal price) {
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
	}

	@JsonIgnore
	public void setPriceAsDouble(Double price) {
		this.price = price != null ? BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) : null;
	}
}