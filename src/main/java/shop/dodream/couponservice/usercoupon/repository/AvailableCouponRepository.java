package shop.dodream.couponservice.usercoupon.repository;

import shop.dodream.couponservice.usercoupon.dto.AvailableCouponResponse;

import java.util.List;

public interface AvailableCouponRepository {
    List<AvailableCouponResponse> findAllAvailableByUserId(Long userId);
}
