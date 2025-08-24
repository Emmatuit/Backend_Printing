package com.example.demo.Dto;

import java.math.BigDecimal;

import com.example.demo.model.ImageInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SelectedSpecificationDto {
	private String specificationName; // e.g., "Color"
	private String optionName; // e.g., "Red"

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal price;

	private ImageInfo image; // if you want to show image too

	public SelectedSpecificationDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectedSpecificationDto(String specificationName, String optionName, BigDecimal price, ImageInfo image) {
		super();
		this.specificationName = specificationName;
		this.optionName = optionName;
		this.price = price;
		this.image = image;
	}

	public String getSpecificationName() {
		return specificationName;
	}

	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ImageInfo getImage() {
		return image;
	}

	public void setImage(ImageInfo image) {
		this.image = image;
	}

}
