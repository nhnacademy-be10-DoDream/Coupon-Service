package shop.dodream.couponservice.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long couponId;
    private Long policyId;
    private String policyName;
    private DiscountType type;
    private Long discountValue;
    private Long minPurchaseAmount;
    private Long maxDiscountAmount;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private Long bookId;
    private Long categoryId;

    public static CouponResponse from(Coupon coupon) {
        CouponPolicy policy = coupon.getCouponPolicy();

        return CouponResponse.builder()
                .couponId(coupon.getCouponId())
                .policyId(policy.getPolicyId())
                .policyName(policy.getName())
                .type(policy.getDiscountType())
                .discountValue(policy.getDiscountValue())
                .minPurchaseAmount(policy.getMinPurchaseAmount())
                .maxDiscountAmount(policy.getMaxDiscountAmount())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .bookId(coupon.getBookId())
                .categoryId(coupon.getCategoryId())
                .build();
    }
}
