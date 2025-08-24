package com.example.demo.Dto;

import java.math.BigDecimal;

public class CouponDto {

	private String code;
	private String discountType;
	private BigDecimal discountValue;
	private BigDecimal minPurchaseAmount;
	private boolean active;

	// Constructors, getters and setters...

	public CouponDto(String code, String discountType, BigDecimal discountValue, BigDecimal minPurchaseAmount,
			boolean active) {
		super();
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.minPurchaseAmount = minPurchaseAmount;
		this.active = active;
	}

	public String getCode() {
		return code;
	}

	public String getDiscountType() {
		return discountType;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public BigDecimal getMinPurchaseAmount() {
		return minPurchaseAmount;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
		this.minPurchaseAmount = minPurchaseAmount;
	}

}
