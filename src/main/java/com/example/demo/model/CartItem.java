package com.example.demo.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int selectedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart; // Ensuring cart_id is always required for cart items

    @Column(nullable = false)
    private double totalPrice;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cart_item_id")
    private List<SpecificationOption> selectedOptions;

    public CartItem() {}

    public CartItem(Product product, int selectedQuantity, double totalPrice, List<SpecificationOption> selectedOptions) {
        this.product = product;
        this.selectedQuantity = selectedQuantity;
        this.totalPrice = totalPrice;
        this.selectedOptions = selectedOptions;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<SpecificationOption> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<SpecificationOption> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
