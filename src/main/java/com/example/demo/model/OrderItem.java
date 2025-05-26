package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price; // Changed from double to BigDecimal

    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subTotal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Add method to calculate subtotal
    public void calculateSubTotal() {
        if (price != null && quantity > 0) {
            this.subTotal = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
    public OrderItem() {
    }

    public OrderItem(Long id, Order order, Product product, int quantity, BigDecimal price) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.setScale(2, RoundingMode.HALF_UP) : null;
    }

    // Backward compatibility for double (if needed)
    @Transient
    public double getPriceAsDouble() {
        return price != null ? price.doubleValue() : 0.0;
    }

    @Transient
    public void setPriceAsDouble(double price) {
        this.price = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}

