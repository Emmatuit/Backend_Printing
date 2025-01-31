package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

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
public class Specification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name; // e.g., "Color", "Size"

	@OneToMany(mappedBy = "specification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<SpecificationOption> options = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	public Specification() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Specification(Long id, String name, Product product) {
		super();
		this.id = id;
		this.name = name;
		this.product = product;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<SpecificationOption> getOptions() {
		return options;
	}

	public Product getProduct() {
		return product;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOptions(List<SpecificationOption> options) {
		this.options = options;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	// Getters and Setters
}
