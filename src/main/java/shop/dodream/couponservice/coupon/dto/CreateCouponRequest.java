package shop.dodream.couponservice.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCouponRequest {

    @NotNull
    private Long policyId;

    @NotNull
    private Long bookId;

    @NotNull
    private Long categoryId;
}
