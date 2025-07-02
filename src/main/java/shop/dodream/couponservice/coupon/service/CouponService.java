package shop.dodream.couponservice.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.dto.CreateCouponRequest;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 생성
    public void createCoupon(CreateCouponRequest request) {
        CouponPolicy policy = couponPolicyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException(request.getPolicyId()));

        Coupon coupon = Coupon.builder()
                .couponPolicy(policy)
                .bookId(request.getBookId())
                .categoryId(request.getCategoryId())
                .build();

        couponRepository.save(coupon);
    }

    // 쿠폰 삭제
    public void deleteCoupon(Long couponId) {
        if (!couponRepository.existsById(couponId)) {
            throw new CouponNotFoundException(couponId);
        }
        couponRepository.deleteById(couponId);
    }

    // 정책별 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponsByPolicy(Long policyId) {
        return couponRepository.findByCouponPolicyPolicyId(policyId).stream()
                .map(CouponResponse::from)
                .toList();
    }

    // 단일 쿠폰 조회
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(Long couponId) {
        return couponRepository.findById(couponId)
                .map(CouponResponse::from)
                .orElseThrow(() -> new CouponNotFoundException(couponId));
    }
}

