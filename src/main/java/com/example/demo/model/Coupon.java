package com.example.demo.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupons")
public class Coupon {

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;  // PERCENTAGE or FIXED_AMOUNT

    private BigDecimal discountValue;

    private BigDecimal minPurchaseAmount;

    private LocalDateTime expiryDate;

    private boolean active;

    private Integer usageLimit;   // max times coupon can be used

    private Integer timesUsed = 0; // track usage count


    public Coupon(Long id, String code, DiscountType discountType, BigDecimal discountValue,
			BigDecimal minPurchaseAmount, LocalDateTime expiryDate, boolean active, Integer usageLimit,
			Integer timesUsed) {
		super();
		this.id = id;
		this.code = code;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.minPurchaseAmount = minPurchaseAmount;
		this.expiryDate = expiryDate;
		this.active = active;
		this.usageLimit = usageLimit;
		this.timesUsed = timesUsed;
	}

	// Constructors, getters and setters...

    public String getCode() {
		return code;
	}

	public DiscountType getDiscountType() {
		return discountType;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getMinPurchaseAmount() {
		return minPurchaseAmount;
	}

	public Integer getTimesUsed() {
		return timesUsed;
	}

	public Integer getUsageLimit() {
		return usageLimit;
	}

	public boolean isActive() {
		return active;
	}

	// Optional helper method
    public boolean isValid() {
        return active && (expiryDate == null || expiryDate.isAfter(LocalDateTime.now()))
               && (usageLimit == null || timesUsed < usageLimit);
    }

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
		this.minPurchaseAmount = minPurchaseAmount;
	}

	public void setTimesUsed(Integer timesUsed) {
		this.timesUsed = timesUsed;
	}

	public void setUsageLimit(Integer usageLimit) {
		this.usageLimit = usageLimit;
	}


}
