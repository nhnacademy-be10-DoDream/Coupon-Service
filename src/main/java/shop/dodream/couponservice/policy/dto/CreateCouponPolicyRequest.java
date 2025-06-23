package shop.dodream.couponservice.policy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.common.DiscountType;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCouponPolicyRequest {

    @NotNull
    private String name;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private Long discountValue;

    private Long minPurchaseAmount;

    private Long maxDiscountAmount;

    @NotNull
    private ZonedDateTime startDate;

    @NotNull
    private ZonedDateTime endDate;
}
