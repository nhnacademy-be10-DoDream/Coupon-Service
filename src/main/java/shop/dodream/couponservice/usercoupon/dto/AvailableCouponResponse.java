package shop.dodream.couponservice.usercoupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.dodream.couponservice.common.DiscountType;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class AvailableCouponResponse {
    private String policyName;
    private DiscountType discountType;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiredAt;
}

