package com.example.demo.Dto;


public class SpecificationOptionDTO {

    private Long id;
    private String name;  // e.g., "Red", "Blue", "Small", "Large"
    private String image; // Image URL or path
    private Double price; // Additional price for this option

    // Default constructor
    public SpecificationOptionDTO() {}

    // Constructor with parameters
    public SpecificationOptionDTO(Long id, String name, String image, Double price) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
