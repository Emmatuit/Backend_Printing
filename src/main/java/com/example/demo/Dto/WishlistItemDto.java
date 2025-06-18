package com.example.demo.Dto;

import java.time.LocalDateTime;

public class WishlistItemDto {
    private Long id;
    private ProductDto product;
    private LocalDateTime addedAt;

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public ProductDto getProduct() { return product; }

    public void setProduct(ProductDto product) { this.product = product; }

    public LocalDateTime getAddedAt() { return addedAt; }

    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
