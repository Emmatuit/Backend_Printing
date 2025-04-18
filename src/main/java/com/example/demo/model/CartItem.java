package com.example.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "designRequest_id", referencedColumnName = "id")
    private DesignRequest designRequest;




    public CartItem() {}


	public CartItem( Product product, int selectedQuantity, double totalPrice,
			List<SpecificationOption> selectedOptions) {
		super();
		this.product = product;
		this.selectedQuantity = selectedQuantity;
		this.totalPrice = totalPrice;
		this.selectedOptions = selectedOptions;
	}


	public Cart getCart() {
		return cart;
	}


	public DesignRequest getDesignRequest() {
		return designRequest;
	}


	public Long getId() {
		return id;
	}


	public Product getProduct() {
		return product;
	}


	public List<SpecificationOption> getSelectedOptions() {
		return selectedOptions;
	}


	public int getSelectedQuantity() {
		return selectedQuantity;
	}


	public double getTotalPrice() {
		return totalPrice;
	}


	public void setCart(Cart cart) {
		this.cart = cart;
	}


	public void setDesignRequest(DesignRequest designRequest) {
		this.designRequest = designRequest;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public void setProduct(Product product) {
		this.product = product;
	}


	public void setSelectedOptions(List<SpecificationOption> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}


	public void setSelectedQuantity(int selectedQuantity) {
		this.selectedQuantity = selectedQuantity;
	}


	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}


}
