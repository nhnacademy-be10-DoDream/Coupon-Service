package shop.dodream.couponservice.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCouponRequest {

    @NotNull
    private Long policyId;

    private Long bookId;

    private Long categoryId;

    public Coupon toEntity(CouponPolicy policy) {
        return Coupon.builder()
                .couponPolicy(policy)
                .bookId(bookId)
                .categoryId(categoryId)
                .build();
    }
}
