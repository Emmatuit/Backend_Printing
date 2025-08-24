package com.example.demo.Dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.model.Order;
import com.example.demo.model.Order.PaymentStatus;
import com.example.demo.model.Order.ShippingMethod;

public class OrderDto {
	private Long orderId;
	private String orderNumber;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private OrderStatus status;

	// Customer
	private String email;
	private String fullName;
	private String phoneNumber;

	// Address
	private String address1;
	private String address2;
	private String state;
	private String postalCode;
	private ShippingMethod shippingMethod;
	private String shippingAddress;

	// Payment
	private String paymentMethod;
	private Order.PaymentStatus paymentStatus;
	private String txRef;
	private String cardLast4;

	// Order Summary
	private BigDecimal totalAmount;
	private BigDecimal taxAmount;
	private BigDecimal shippingFee;
	private BigDecimal grandTotal;

	// Items
	private List<OrderItemDto> items;

	// Tracking
	private String trackingNumber; // New
	private String paymentId; // ✅ Flutterwave Payment ID

	private String couponCode; // New
	private BigDecimal discountAmount; // New

	public OrderDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrderDto(Long orderId, String orderNumber, LocalDateTime createdAt, LocalDateTime updatedAt,
			OrderStatus status, String email, String fullName, String phoneNumber, String address1, String address2,
			String state, String postalCode, ShippingMethod shippingMethod, String shippingAddress,
			String paymentMethod, PaymentStatus paymentStatus, String txRef, String cardLast4, BigDecimal totalAmount,
			BigDecimal taxAmount, BigDecimal shippingFee, BigDecimal grandTotal, List<OrderItemDto> items,
			String trackingNumber, String paymentId, String couponCode, BigDecimal discountAmount) {
		super();
		this.orderId = orderId;
		this.orderNumber = orderNumber;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
		this.email = email;
		this.fullName = fullName;
		this.phoneNumber = phoneNumber;
		this.address1 = address1;
		this.address2 = address2;
		this.state = state;
		this.postalCode = postalCode;
		this.shippingMethod = shippingMethod;
		this.shippingAddress = shippingAddress;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.txRef = txRef;
		this.cardLast4 = cardLast4;
		this.totalAmount = totalAmount;
		this.taxAmount = taxAmount;
		this.shippingFee = shippingFee;
		this.grandTotal = grandTotal;
		this.items = items;
		this.trackingNumber = trackingNumber;
		this.paymentId = paymentId;
		this.couponCode = couponCode;
		this.discountAmount = discountAmount;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	// Getters and Setters for coupon fields
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

	public List<OrderItemDto> getItems() {
		return items;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public Order.PaymentStatus getPaymentStatus() {
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

	// Getters and setters for new fields
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
		return totalAmount;
	}

	public String getTxRef() {
		return txRef;
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
		this.discountAmount = discountAmount != null ? discountAmount.setScale(2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;
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

	public void setItems(List<OrderItemDto> items) {
		this.items = items;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setPaymentStatus(Order.PaymentStatus paymentStatus) {
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
		this.totalAmount = totalAmount;
	}

	public void setTxRef(String txRef) {
		this.txRef = txRef;
	}

	public String getCardLast4() {
		return cardLast4;
	}

	public void setCardLast4(String cardLast4) {
		this.cardLast4 = cardLast4;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
