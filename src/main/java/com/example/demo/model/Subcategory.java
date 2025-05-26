package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Subcategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "subcategory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Product> products = new ArrayList<>();

	public Subcategory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Subcategory(Long id, String name, Category category, List<Product> products) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
		this.products = products;
	}

	public Category getCategory() {
		return category;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	// Getters and Setters

}
