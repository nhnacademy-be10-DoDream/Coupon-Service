package shop.dodream.couponservice.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import shop.dodream.couponservice.common.DiscountType;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    @NotNull
    private Long couponId;
    @NotNull
    private Long policyId;
    @NotNull
    private String policyName;
    @NotNull
    private DiscountType type;

    private Long discountValue;

    private Long minPurchaseAmount;

    private Long maxDiscountAmount;
    @NotNull
    private ZonedDateTime startDate;
    @NotNull
    private ZonedDateTime endDate;
    private Long bookId;
    private Long categoryId;
}
