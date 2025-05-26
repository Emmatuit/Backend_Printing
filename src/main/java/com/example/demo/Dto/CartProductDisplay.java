package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CartProductDisplay {
	private String name;
	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal baseprice; // Changed from Double to BigDecimal

	// Constructors
	public CartProductDisplay() {
	}

	public CartProductDisplay(String name, String description, BigDecimal baseprice) {
		this.name = name;
		this.description = description;
		this.baseprice = baseprice != null ? baseprice.setScale(2, RoundingMode.HALF_UP) : null;
	}

	// Backward compatibility for Double
	@JsonIgnore
	public Double getBasepriceAsDouble() {
		return baseprice != null ? baseprice.doubleValue() : null;
	}

	@JsonIgnore
	public void setBasepriceAsDouble(Double baseprice) {
		this.baseprice = baseprice != null ? BigDecimal.valueOf(baseprice).setScale(2, RoundingMode.HALF_UP) : null;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBaseprice() {
		return baseprice;
	}

	public void setBaseprice(BigDecimal baseprice) {
		this.baseprice = baseprice != null ? baseprice.setScale(2, RoundingMode.HALF_UP) : null;
	}
}
