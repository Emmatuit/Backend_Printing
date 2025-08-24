package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Product { // Updated to "Product" from "Products"
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;

	@Column(precision = 19, scale = 2)
	private BigDecimal baseprice;
	private Integer MinOrderquantity;
	private Integer MaxQuantity;
	private Integer IncrementStep;

	// Store the encrypted images as a list of strings
	@ElementCollection
	private List<ImageInfo> encryptedImages = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subcategory_id", nullable = true)
	@JsonIgnore
	private Subcategory subcategory;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Specification> specifications = new ArrayList<>();

	// ✅ Track total views
	@Column(nullable = false)
	private Long views = 0L;

	// ✅ Track when the product was created
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// Optional: updatedAt for future use
	@Column
	private LocalDateTime updatedAt;

	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean deleted) {
		isDeleted = deleted;
	}

	// Default constructor
	public Product() {
	}

	public Product(Long id, String name, String description, BigDecimal baseprice, Integer minOrderquantity,
			Integer maxQuantity, Integer incrementStep, List<ImageInfo> encryptedImages, Subcategory subcategory,
			Category category, List<Specification> specifications, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.baseprice = baseprice != null ? baseprice.setScale(2, RoundingMode.HALF_UP) : null;
		MinOrderquantity = minOrderquantity;
		MaxQuantity = maxQuantity;
		IncrementStep = incrementStep;
		this.encryptedImages = encryptedImages;
		this.subcategory = subcategory;
		this.category = category;
		this.specifications = specifications;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public BigDecimal getBaseprice() {
		return baseprice;
	}

	public Category getCategory() {
		return category;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getDescription() {
		return description;
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

	// Getter and Setter for specifications
	public List<Specification> getSpecifications() {
		return specifications;
	}

	public Subcategory getSubcategory() {
		return subcategory;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Long getViews() {
		return views;
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public void setBaseprice(BigDecimal baseprice) {
		this.baseprice = baseprice;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	// Constructor with parameters

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ImageInfo> getEncryptedImages() {
		return encryptedImages;
	}

	public void setEncryptedImages(List<ImageInfo> encryptedImages) {
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

	public void setSpecifications(List<Specification> specifications) {
		this.specifications = specifications;
	}

	public void setSubcategory(Subcategory subcategory) {
		this.subcategory = subcategory;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", baseprice=" + baseprice
				+ ", MinOrderquantity=" + MinOrderquantity + ", MaxQuantity=" + MaxQuantity + ", IncrementStep="
				+ IncrementStep + ", encryptedImages=" + encryptedImages + ", subcategory=" + subcategory
				+ ", category=" + category + ", specifications=" + specifications + ", views=" + views + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
