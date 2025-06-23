package shop.dodream.couponservice.usercoupon.dto;

import lombok.*;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;

import java.time.ZonedDateTime;

@Getter
@Builder
public class UserCouponResponse {
    private final Long id;
    private final Long userId;
    private final CouponResponse coupon;
    private final ZonedDateTime issuedAt;
    private final ZonedDateTime usedAt;

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return UserCouponResponse.builder()
                .id(userCoupon.getUserCouponId())
                .userId(userCoupon.getUserId())
                .coupon(CouponResponse.from(userCoupon.getCoupon()))
                .issuedAt(userCoupon.getIssuedAt())
                .usedAt(userCoupon.getUsedAt())
                .build();
    }
}

