package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.ImageInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ProductDto {
	private long id;
	private String name;
	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal basePrice; // Changed from Double to BigDecimal
	private Integer minOrderQuantity;
	private Integer maxQuantity;
	private Integer incrementStep;
	private Long subcategoryId;
	private Long categoryId;
	private List<ImageInfo> encryptedImages;
	private List<SpecificationDTO> specifications;
	private Long views;
	private LocalDateTime createdAt;
	private String categoryName;
	private String subcategoryName;

	// Constructors
	public ProductDto() {
		this.encryptedImages = new ArrayList<>();
		this.specifications = new ArrayList<>();
	}

	public ProductDto(long id, String name, String description, BigDecimal basePrice, Integer minOrderQuantity,
			Integer maxQuantity, Integer incrementStep, Long subcategoryId, Long categoryId,
			List<ImageInfo> encryptedImages, List<SpecificationDTO> specifications, Long views,
			LocalDateTime createdAt) {
		this();
		this.id = id;
		this.name = name;
		this.description = description;
		this.basePrice = basePrice != null ? basePrice.setScale(2, RoundingMode.HALF_UP) : null;
		this.minOrderQuantity = minOrderQuantity;
		this.maxQuantity = maxQuantity;
		this.incrementStep = incrementStep;
		this.subcategoryId = subcategoryId;
		this.categoryId = categoryId;
		if (encryptedImages != null) {
			this.encryptedImages = encryptedImages;
		}
		if (specifications != null) {
			this.specifications = specifications;
		}
		this.views = views;
		this.createdAt = createdAt;

	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getDescription() {
		return description;
	}

	public List<ImageInfo> getEncryptedImages() {
		return encryptedImages;
	}

	// Backward compatibility for Double
//    @JsonIgnore
//    public Double getBasePriceAsDouble() {
//        return basePrice != null ? basePrice.doubleValue() : null;
//    }
//
//
//    @JsonIgnore
//    public void setBasePriceAsDouble(Double price) {
//        this.basePrice = price != null ?
//            BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP) : null;
//    }
	// Getters and Setters
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

	public List<SpecificationDTO> getSpecifications() {
		return specifications;
	}

	public Long getSubcategoryId() {
		return subcategoryId;
	}

	public Long getViews() {
		return views;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice != null ? basePrice.setScale(2, RoundingMode.HALF_UP) : null;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEncryptedImages(List<ImageInfo> encryptedImages) {
		this.encryptedImages = encryptedImages != null ? encryptedImages : new ArrayList<>();
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
		this.specifications = specifications != null ? specifications : new ArrayList<>();
	}

	public void setSubcategoryId(Long subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSubcategoryName() {
		return subcategoryName;
	}

	public void setSubcategoryName(String subcategoryName) {
		this.subcategoryName = subcategoryName;
	}

}
