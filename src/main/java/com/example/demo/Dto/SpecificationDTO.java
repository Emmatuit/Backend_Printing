package com.example.demo.Dto;

import java.util.List;

public class SpecificationDTO {

	private Long id;
	private String name; // e.g., "Color", "Size"
	private List<SpecificationOptionDTO> options; // List of options for this specification

	public SpecificationDTO() {
	}

	public SpecificationDTO(Long id, String name, List<SpecificationOptionDTO> options) {
		this.id = id;
		this.name = name;
		this.options = options;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<SpecificationOptionDTO> getOptions() {
		return options;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOptions(List<SpecificationOptionDTO> options) {
		this.options = options;
	}
}
