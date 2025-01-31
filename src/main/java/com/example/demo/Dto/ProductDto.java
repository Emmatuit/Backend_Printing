package com.example.demo.Dto;

import java.util.List;

public class ProductDto {
	private long id;
	private String name;
	private String description;
	private Double basePrice;
	private Integer minOrderQuantity;
	private Integer maxQuantity;
	private Integer incrementStep;
	private Long subcategoryId; // ID of the subcategory to associate the product with
	private Long categoryId;
	private List<String> encryptedImages; // List of image strings
	private List<SpecificationDTO> specifications;

	public ProductDto() {
		super();
		// TODO Auto-generated constructor stub
	}

//	public ProductDto(long id, String name, String description, Double basePrice, Integer minOrderQuantity,
//			Integer maxQuantity, Integer incrementStep, Long subcategoryId, Long categoryId,
//			List<String> encryptedImages) {
//
//		this.id = id;
//		this.name = name;
//		this.description = description;
//		this.basePrice = basePrice;
//		this.minOrderQuantity = minOrderQuantity;
//		this.maxQuantity = maxQuantity;
//		this.incrementStep = incrementStep;
//		this.subcategoryId = subcategoryId;
//		this.categoryId = categoryId;
//		this.encryptedImages = encryptedImages;
//
//	}

	public ProductDto(long id, String name, String description, Double basePrice, Integer minOrderQuantity,
			Integer maxQuantity, Integer incrementStep, Long subcategoryId, Long categoryId,
			List<String> encryptedImages, List<SpecificationDTO> specifications) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.basePrice = basePrice;
		this.minOrderQuantity = minOrderQuantity;
		this.maxQuantity = maxQuantity;
		this.incrementStep = incrementStep;
		this.subcategoryId = subcategoryId;
		this.categoryId = categoryId;
		this.encryptedImages = encryptedImages;
		this.specifications = specifications;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getEncryptedImages() {
		return encryptedImages;
	}

	public long getId() {
		return id;
	}

	public Integer getIncrementStep() {
		return incrementStep;
	}

	public Integer getMaxQuantity() {
		return maxQuantity;
	}

	public Integer getMinOrderQuantity() {
		return minOrderQuantity;
	}

	public String getName() {
		return name;
	}

	// Getters and setters
	public List<SpecificationDTO> getSpecifications() {
		return specifications;
	}

	public Long getSubcategoryId() {
		return subcategoryId;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEncryptedImages(List<String> encryptedImages) {
		this.encryptedImages = encryptedImages;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIncrementStep(Integer incrementStep) {
		this.incrementStep = incrementStep;
	}

	public void setMaxQuantity(Integer maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public void setMinOrderQuantity(Integer minOrderQuantity) {
		this.minOrderQuantity = minOrderQuantity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpecifications(List<SpecificationDTO> specifications) {
		this.specifications = specifications;
	}

	public void setSubcategoryId(Long subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

}
