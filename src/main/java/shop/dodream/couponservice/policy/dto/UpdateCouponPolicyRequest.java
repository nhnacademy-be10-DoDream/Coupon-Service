package shop.dodream.couponservice.policy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateCouponPolicyRequest {

    @NotBlank
    private String name;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @Min(0)
    private Long discountValue;

    @Min(0)
    @NotNull
    private Long minPurchaseAmount;

    @Min(0)
    @NotNull
    private Long maxDiscountAmount;

    @NotNull
    private ExpiredStrategy expiredStrategy;

    private ZonedDateTime fixedDate;

    @Min(0)
    private Long plusDay;




}
