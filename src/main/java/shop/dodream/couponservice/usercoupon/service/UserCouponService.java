package shop.dodream.couponservice.usercoupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.InvalidCouponPolicyException;
import shop.dodream.couponservice.exception.UnauthorizedUserCouponAccessException;
import shop.dodream.couponservice.exception.UserCouponNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
import shop.dodream.couponservice.usercoupon.dto.UserCouponResponse;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.repository.UserCouponRepository;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;


    public void issuedCoupon(IssueCouponRequest request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException(request.getCouponId()));

        CouponPolicy couponPolicy = coupon.getCouponPolicy();
        ZonedDateTime now = ZonedDateTime.now();

        if (couponPolicy.getStartDate().isAfter(now) || couponPolicy.getEndDate().isBefore(now)) {
            throw new InvalidCouponPolicyException("Invalid coupon policy");
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(request.getUserId())
                .coupon(coupon)
                .issuedAt(ZonedDateTime.now())
                .build();

        userCouponRepository.save(userCoupon);
    }

    @Transactional(readOnly = true)
    public List<UserCouponResponse> getAvailableCoupons(Long userId) {
        // todo
        return List.of();
    }


    public void useCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException(userCouponId));
        if (!userCoupon.getUserId().equals(userId)) {
            throw new UnauthorizedUserCouponAccessException(userId, userCouponId);
        }

        userCoupon.use();
    }
}
