package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SpecificationOption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String image;
	private Double price;

	@ManyToOne
	@JoinColumn(name = "specification_id") // Foreign key to Specification
	private Specification specification;

	// Getters and Setters

	public SpecificationOption() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SpecificationOption(Long id, String name, String image, Double price, Specification specification) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
		this.price = price;
		this.specification = specification;
	}

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

	public Specification getSpecification() {
		return specification;
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

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}
}
