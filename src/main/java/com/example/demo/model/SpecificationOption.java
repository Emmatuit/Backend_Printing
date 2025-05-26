package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class SpecificationOption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String image;

	@Column(precision = 19, scale = 2) // Ensures proper decimal storage
	private BigDecimal price; // Changed from Double to BigDecimal

	@ManyToOne
	@JoinColumn(name = "specification_id")
	private Specification specification;

	// Constructors
	public SpecificationOption() {
	}

	public SpecificationOption(Long id, String name, String image, BigDecimal price, Specification specification) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
		this.specification = specification;
	}

	// Backward compatibility for Double
	public void setPrice(Double price) {
		this.price = price != null ? BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) : null;
	}

	@Transient
	public Double getPriceAsDouble() {
		return price != null ? price.doubleValue() : null;
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

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}
}