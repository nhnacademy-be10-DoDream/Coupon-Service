package shop.dodream.couponservice.usercoupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import shop.dodream.couponservice.common.DiscountType;

import java.time.ZonedDateTime;

@Getter
@Setter
public class AvailableCouponResponse {
    private String policyName;
    private DiscountType discountType;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiredAt;

    @QueryProjection
    public AvailableCouponResponse(
            String policyName,
            DiscountType discountType,
            Long discountValue,
            Long minPurchaseAmount,
            Long maxDiscountAmount,
            ZonedDateTime issuedAt,
            ZonedDateTime expiredAt
    ) {
        this.policyName = policyName;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPurchaseAmount = minPurchaseAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
    }
}

