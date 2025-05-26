package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
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
	private String description;

	// Change from single image to list of images
	@ElementCollection
	private List<String> encryptedImages = new ArrayList<>();

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JsonManagedReference
	private List<Subcategory> subcategories = new ArrayList<>();

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	private String name;
//	private String description; // Description of the category
//
//	// Store the encrypted image as a string
//	private String encryptedImage; // To store the encrypted image
//
//	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//	@JsonManagedReference // Manage reference here
//	private List<Subcategory> subcategories = new ArrayList<>(); // Relationship with subcategories

	// Default constructor
	public Category() {
	}

	public Category(Long id, String name, String description, List<String> encryptedImages,
			List<Subcategory> subcategories) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.encryptedImages = encryptedImages;
		this.subcategories = subcategories;
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

	public String getName() {
		return name;
	}

	public List<Subcategory> getSubcategories() {
		return subcategories;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEncryptedImages(List<String> encryptedImages) {
		this.encryptedImages = encryptedImages;
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
