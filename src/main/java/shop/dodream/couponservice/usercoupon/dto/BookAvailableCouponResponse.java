package shop.dodream.couponservice.usercoupon.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BookAvailableCouponResponse {

    private Long couponId;
    private String policyName;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private Long finalPrice;

    @QueryProjection
    public BookAvailableCouponResponse(
            Long couponId,
            String policyName,
            Long discountValue,
            Long minPurchaseAmount,
            Long maxDiscountAmount
    ) {
        this.couponId = couponId;
        this.policyName = policyName;
        this.discountValue = discountValue;
        this.minPurchaseAmount = minPurchaseAmount;
        this.maxDiscountAmount = maxDiscountAmount;
    }

}
