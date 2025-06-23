package shop.dodream.couponservice.usercoupon.dto;

import lombok.*;
import shop.dodream.couponservice.coupon.dto.CouponResponse;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponResponse {
    private Long id;
    private Long userId;
    private CouponResponse coupon;
    private ZonedDateTime issuedAt;
    private ZonedDateTime usedAt;
}

