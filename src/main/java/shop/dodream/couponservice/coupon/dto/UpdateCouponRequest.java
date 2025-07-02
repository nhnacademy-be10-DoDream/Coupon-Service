package shop.dodream.couponservice.coupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCouponRequest {
    @NotNull
    private Long policyId;

    private Long bookId;

    private Long categoryId;
}
