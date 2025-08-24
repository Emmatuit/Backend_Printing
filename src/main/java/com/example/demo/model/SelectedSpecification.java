package com.example.demo.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SelectedSpecification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String specificationName;
	private String optionName;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal price;

	@Embedded
	private ImageInfo image;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	private OrderItem orderItem;

	public SelectedSpecification() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectedSpecification(Long id, String specificationName, String optionName, BigDecimal price,
			ImageInfo image, OrderItem orderItem) {
		super();
		this.id = id;
		this.specificationName = specificationName;
		this.optionName = optionName;
		this.price = price;
		this.image = image;
		this.orderItem = orderItem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSpecificationName() {
		return specificationName;
	}

	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ImageInfo getImage() {
		return image;
	}

	public void setImage(ImageInfo image) {
		this.image = image;
	}

	public OrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	// Constructors, getters, setters

}
