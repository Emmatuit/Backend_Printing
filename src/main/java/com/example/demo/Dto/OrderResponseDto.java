package com.example.demo.Dto;


import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;


public class OrderResponseDto {
    private Long id;
    private String username;
    private List<OrderItemDto> items;
    private double totalAmount;
    private String shippingAddress;
    private String status;
    private LocalDateTime createdAt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<OrderItemDto> getItems() {
		return items;
	}
	public void setItems(List<OrderItemDto> items) {
		this.items = items;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public OrderResponseDto(Long id, String username, List<OrderItemDto> items, double totalAmount,
			String shippingAddress, String status, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.username = username;
		this.items = items;
		this.totalAmount = totalAmount;
		this.shippingAddress = shippingAddress;
		this.status = status;
		this.createdAt = createdAt;
	}
	public OrderResponseDto() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
