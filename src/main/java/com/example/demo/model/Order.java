package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.Enum.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

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

	// Add enum for PaymentStatus
	public enum PaymentStatus {
		PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED
	}

	public enum ShippingMethod {
	    PICKUP, DELIVERY
	}

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


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ShippingMethod shippingMethod;

	// shipping information

	@Column
	private String shippingAddress;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal grandTotal;

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

	@Column(nullable = false, unique = true, updatable = false)
	private String orderNumber;

	// Payment fields
	@Column
	private String paymentMethod;

	@Column
	private String paymentId; // For payment processor reference

	private String couponCode;



	private BigDecimal discountAmount;

	@Column(unique = true)
	private String txRef;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;

	@Column
	private LocalDateTime paymentDate;


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status = OrderStatus.PENDING;


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Column(nullable = false)
	private LocalDateTime updatedAt = LocalDateTime.now();

	// Add pre-persist and pre-update methods


	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Order(Long id, UserEntity user, List<OrderItem> items, BigDecimal totalAmount,
            String fullName, String email, String phoneNumber, String address1,
            String address2, String state, String postalCode,
            OrderStatus status, String orderNumber, ShippingMethod shippingMethod,
            String shippingAddress, String couponCode, BigDecimal discountAmount, LocalDateTime createdAt, String txRef) {

   this.id = id;
   this.user = user;
   this.items = items;
   this.totalAmount = totalAmount != null ? totalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
   this.fullName = fullName;
   this.email = email;
   this.phoneNumber = phoneNumber;
   this.address1 = address1;
   this.address2 = address2;
   this.state = state;

   this.postalCode = postalCode;
   this.status = status != null ? status : OrderStatus.PENDING;
   this.orderNumber = orderNumber;
   this.shippingMethod = shippingMethod != null ? shippingMethod : ShippingMethod.DELIVERY;
   this.shippingAddress = shippingAddress;
   this.createdAt = createdAt;

   this.couponCode = couponCode;
   this.discountAmount = discountAmount != null ? discountAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
   this.txRef = txRef;
}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}


	public String getCouponCode() {
        return couponCode;
    }

    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public Long getId() {
		return id;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public BigDecimal getShippingFee() {
		return shippingFee;
	}

	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	public String getState() {
		return state;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount != null ? totalAmount : BigDecimal.ZERO;
	}

	// Backward compatibility for double (if needed)
	@Transient
	public double getTotalAmountAsDouble() {
		return totalAmount != null ? totalAmount.doubleValue() : 0.0;
	}

	public String getTxRef() {
		return txRef;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public UserEntity getUser() {
		return user;
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		// Ensure totalAmount is properly set before persisting
		if (this.totalAmount == null) {

		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount != null ? discountAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public void setShippingFee(BigDecimal shippingFee) {
		this.shippingFee = shippingFee;
	}

	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount != null ? totalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	@Transient
	public void setTotalAmountAsDouble(double amount) {
		this.totalAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
	}

	public void setTxRef(String txRef) {
		this.txRef = txRef;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}





}
