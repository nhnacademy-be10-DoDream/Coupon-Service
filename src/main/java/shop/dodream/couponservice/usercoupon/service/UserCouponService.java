package shop.dodream.couponservice.usercoupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.InvalidCouponPolicyException;
import shop.dodream.couponservice.exception.UnauthorizedUserCouponAccessException;
import shop.dodream.couponservice.exception.UserCouponNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.usercoupon.dto.AvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
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

        CouponPolicy policy = coupon.getCouponPolicy();

        ZonedDateTime issuedAt = ZonedDateTime.now();
        ZonedDateTime expiredAt;

        if (policy.getExpiredStrategy() == ExpiredStrategy.FIXED) {
            expiredAt = policy.getFixedDate();
        } else if (policy.getExpiredStrategy() == ExpiredStrategy.PLUS) {
            expiredAt = issuedAt.plusDays(policy.getPlusDay());
        } else {
            throw new InvalidCouponPolicyException("유효하지 않은 쿠폰 만료 정책입니다.");
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(request.getUserId())
                .coupon(coupon)
                .issuedAt(ZonedDateTime.now())
                .expiredAt(expiredAt)
                .build();

        userCouponRepository.save(userCoupon);
    }

    // 사용자 마이페이지 사용가능한 전체 쿠폰 조회 페이징?
    @Transactional(readOnly = true)
    public List<AvailableCouponResponse> getAvailableCoupons(String userId) {

        List<AvailableCouponResponse> availableCoupons = userCouponRepository.findAllAvailableByUserId(userId);
        return availableCoupons;
    }

    // TODO 상품 별 사용가능한 쿠폰 조회 만들어야함



    public void useCoupon(String userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException(userCouponId));
        if (!userCoupon.getUserId().equals(userId)) {
            throw new UnauthorizedUserCouponAccessException(userId, userCouponId);
        }

        userCoupon.use();
    }
}
