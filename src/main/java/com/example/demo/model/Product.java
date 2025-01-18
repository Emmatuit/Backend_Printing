package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Product { // Updated to "Product" from "Products"
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;
	private Double baseprice;
	private Integer MinOrderquantity;
	private Integer MaxQuantity;
	private Integer IncrementStep;

	// Store the encrypted images as a list of strings
	@ElementCollection
	private List<String> encryptedImages = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "subcategory_id")
	private Subcategory subcategory;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	// Default constructor
	public Product() {
	}

	public Product(Long id, String name, String description, Double baseprice, Integer minOrderquantity,
			Integer maxQuantity, Integer incrementStep, List<String> encryptedImages, Subcategory subcategory,
			Category category) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.baseprice = baseprice;
		MinOrderquantity = minOrderquantity;
		MaxQuantity = maxQuantity;
		IncrementStep = incrementStep;
		this.encryptedImages = encryptedImages;
		this.subcategory = subcategory;
		this.category = category;

	}

	public Double getBaseprice() {
		return baseprice;
	}

	public Category getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getEncryptedImages() {
		return encryptedImages;
	}

	public Long getId() {
		return id;
	}

	public Integer getIncrementStep() {
		return IncrementStep;
	}

	public Integer getMaxQuantity() {
		return MaxQuantity;
	}

	public Integer getMinOrderquantity() {
		return MinOrderquantity;
	}

	public String getName() {
		return name;
	}

	public Subcategory getSubcategory() {
		return subcategory;
	}

	public void setBaseprice(Double baseprice) {
		this.baseprice = baseprice;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	// Constructor with parameters

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEncryptedImages(List<String> encryptedImages) {
		this.encryptedImages = encryptedImages;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIncrementStep(Integer incrementStep) {
		IncrementStep = incrementStep;
	}

	public void setMaxQuantity(Integer maxQuantity) {
		MaxQuantity = maxQuantity;
	}

	public void setMinOrderquantity(Integer minOrderquantity) {
		MinOrderquantity = minOrderquantity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubcategory(Subcategory subcategory) {
		this.subcategory = subcategory;
	}

}
