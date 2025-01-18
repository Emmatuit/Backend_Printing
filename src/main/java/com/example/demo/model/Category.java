package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description; // Description of the category

	// Store the encrypted image as a string
	private String encryptedImage; // To store the encrypted image

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference // Manage reference here
	private List<Subcategory> subcategories = new ArrayList<>(); // Relationship with subcategories

	// Default constructor
	public Category() {
	}

	// Constructor with parameters
	public Category(Long id, String name, String description, String encryptedImage) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.encryptedImage = encryptedImage;
	}

	public String getDescription() {
		return description;
	}

	public String getEncryptedImage() { // Getter for the encrypted image
		return encryptedImage;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Subcategory> getSubcategories() {
		return subcategories;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEncryptedImage(String encryptedImage) { // Setter for the encrypted image
		this.encryptedImage = encryptedImage;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubcategories(List<Subcategory> subcategories) {
		this.subcategories = subcategories;
	}
}
