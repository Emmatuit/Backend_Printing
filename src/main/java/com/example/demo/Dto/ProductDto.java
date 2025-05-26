package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
	private List<String> encryptedImages;
	private List<SpecificationDTO> specifications;
	private Long views;
	private LocalDateTime createdAt;

	// Constructors
	public ProductDto() {
		this.encryptedImages = new ArrayList<>();
		this.specifications = new ArrayList<>();
	}

	public ProductDto(long id, String name, String description, BigDecimal basePrice, Integer minOrderQuantity,
			Integer maxQuantity, Integer incrementStep, Long subcategoryId, Long categoryId,
			List<String> encryptedImages, List<SpecificationDTO> specifications, Long views, LocalDateTime createdAt) {
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

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice != null ? basePrice.setScale(2, RoundingMode.HALF_UP) : null;
	}

	public Integer getMinOrderQuantity() {
		return minOrderQuantity;
	}

	public void setMinOrderQuantity(Integer minOrderQuantity) {
		this.minOrderQuantity = minOrderQuantity;
	}

	public Integer getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(Integer maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public Integer getIncrementStep() {
		return incrementStep;
	}

	public void setIncrementStep(Integer incrementStep) {
		this.incrementStep = incrementStep;
	}

	public Long getSubcategoryId() {
		return subcategoryId;
	}

	public void setSubcategoryId(Long subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public List<String> getEncryptedImages() {
		return encryptedImages;
	}

	public void setEncryptedImages(List<String> encryptedImages) {
		this.encryptedImages = encryptedImages != null ? encryptedImages : new ArrayList<>();
	}

	public List<SpecificationDTO> getSpecifications() {
		return specifications;
	}

	public void setSpecifications(List<SpecificationDTO> specifications) {
		this.specifications = specifications != null ? specifications : new ArrayList<>();
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
