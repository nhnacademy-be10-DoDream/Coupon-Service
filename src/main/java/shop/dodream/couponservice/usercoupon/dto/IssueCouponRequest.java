package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueCouponRequest {
    @NotBlank
    private String userId;

    @NotNull
    private Long couponId;

}