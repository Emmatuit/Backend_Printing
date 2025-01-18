package com.example.demo.Dto;

public class CartProductDisplay {
	private String name;
	private String description;
	private Double baseprice;

	public CartProductDisplay() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CartProductDisplay(String name, String description, Double baseprice) {
		super();
		this.name = name;
		this.description = description;
		this.baseprice = baseprice;
	}

	public Double getBaseprice() {
		return baseprice;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public void setBaseprice(Double baseprice) {
		this.baseprice = baseprice;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

}
