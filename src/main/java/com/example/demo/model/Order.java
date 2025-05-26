package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.Enum.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user; // User who placed the order

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items; // List of ordered items

	   @Column(nullable = false, precision = 19, scale = 2)
	    private BigDecimal totalAmount; // Properly defined BigDecimal
	
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grandTotal;
    
    //shipping information


	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String address1;

	@Column
	private String address2;

	@Column(nullable = false)
	private String state;

	@Column(nullable = false)
	private String postalCode;


    // Payment fields
    @Column
    private String paymentMethod;
    
    @Column
    private String paymentId; // For payment processor reference
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Add enum for PaymentStatus
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }

    // Add pre-persist and pre-update methods

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Ensure totalAmount is properly set before persisting
        if (this.totalAmount == null) {
            this.totalAmount = calculateTotalAmount();
        }
    }

	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}

    public Order(Long id, UserEntity user, List<OrderItem> items, BigDecimal totalAmount, 
            String fullName, String email, String phoneNumber, String address1, 
            String address2, String state, String postalCode, OrderStatus status) {
    this.id = id;
    this.user = user;
    this.items = items;
    this.totalAmount = totalAmount != null ? 
        totalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    this.fullName = fullName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.address1 = address1;
    this.address2 = address2;
    this.state = state;
    this.postalCode = postalCode;
    this.status = status != null ? status : OrderStatus.PENDING;
    
    }
    
    
    
    public BigDecimal getTotalAmount() {
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? 
            totalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    // Backward compatibility for double (if needed)
    @Transient
    public double getTotalAmountAsDouble() {
        return totalAmount != null ? totalAmount.doubleValue() : 0.0;
    }

    @Transient
    public void setTotalAmountAsDouble(double amount) {
        this.totalAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }
    
	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public Long getId() {
		return id;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getState() {
		return state;
	}

	public OrderStatus getStatus() {
		return status;
	}

	

	public UserEntity getUser() {
		return user;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	

	public void setUser(UserEntity user) {
		this.user = user;
	}

	    public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(BigDecimal shippingFee) {
		this.shippingFee = shippingFee;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

		// Helper method to calculate total from order items
	    public BigDecimal calculateTotalAmount() {
	        if (items == null || items.isEmpty()) {
	            return BigDecimal.ZERO;
	        }
	        return items.stream()
	                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
	                .reduce(BigDecimal.ZERO, BigDecimal::add)
	                .setScale(2, RoundingMode.HALF_UP);
	    }

}
