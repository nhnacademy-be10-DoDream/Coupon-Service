package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueCouponRequest {
    @NotNull
    private String userId;
    @NotNull
    private Long couponId;

}