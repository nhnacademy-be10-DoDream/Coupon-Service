package shop.dodream.couponservice.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CouponPolicyResponse {

    private Long id;

    private String name;

    private DiscountType discountType;

    private Long discountValue;

    private Long minPurchaseAmount;

    private Long maxDiscountAmount;

    private ExpiredStrategy expiredStrategy;

    private ZonedDateTime fixedDate;

    private Long plusDay;

    public static CouponPolicyResponse from(CouponPolicy couponPolicy) {
        return CouponPolicyResponse.builder()
                .id(couponPolicy.getPolicyId())
                .name(couponPolicy.getName())
                .discountType(couponPolicy.getDiscountType())
                .discountValue(couponPolicy.getDiscountValue())
                .minPurchaseAmount(couponPolicy.getMinPurchaseAmount())
                .maxDiscountAmount(couponPolicy.getMaxDiscountAmount())
                .expiredStrategy(couponPolicy.getExpiredStrategy())
                .fixedDate(couponPolicy.getFixedDate())
                .plusDay(couponPolicy.getPlusDay())
                .build();
    }
}
