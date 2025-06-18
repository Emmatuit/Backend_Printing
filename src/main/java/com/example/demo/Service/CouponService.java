package com.example.demo.Service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.CouponRepository;
import com.example.demo.model.Coupon;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public BigDecimal applyDiscount(Coupon coupon, BigDecimal amount) {
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            BigDecimal discountPercent = coupon.getDiscountValue().divide(BigDecimal.valueOf(100));
            return amount.multiply(BigDecimal.ONE.subtract(discountPercent));
        } else if (coupon.getDiscountType() == Coupon.DiscountType.FIXED_AMOUNT) {
            BigDecimal discountedAmount = amount.subtract(coupon.getDiscountValue());
            return discountedAmount.compareTo(BigDecimal.ZERO) > 0 ? discountedAmount : BigDecimal.ZERO;
        } else {
            return amount;
        }
    }

    // Optionally, increment coupon usage after successful order
    public void incrementUsage(Coupon coupon) {
        coupon.setTimesUsed(coupon.getTimesUsed() + 1);
        couponRepository.save(coupon);
    }

    public Optional<Coupon> validateCoupon(String code, BigDecimal cartTotal) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);

        if (couponOpt.isEmpty()) {
            return Optional.empty();
        }

        Coupon coupon = couponOpt.get();

        if (!coupon.isValid() || (coupon.getMinPurchaseAmount() != null && cartTotal.compareTo(coupon.getMinPurchaseAmount()) < 0)) {
            return Optional.empty();
        }

        return Optional.of(coupon);
    }
}
