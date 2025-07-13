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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 생성
    public void createCoupon(CreateCouponRequest request) {
        CouponPolicy policy = couponPolicyRepository.findByPolicyIdAndDeletedFalse(request.getPolicyId())
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
        if (!couponRepository.existsByCouponIdAndDeletedFalse(couponId)) {
            throw new CouponNotFoundException(couponId);
        }
        couponRepository.deleteById(couponId);
    }

    // 정책별 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponsByPolicy(Long policyId) {
        return couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(policyId).stream()
                .map(CouponResponse::from)
                .toList();
    }

    // 단일 쿠폰 조회
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(Long couponId) {
        return couponRepository.findByCouponIdAndDeletedFalse(couponId)
                .map(CouponResponse::from)
                .orElseThrow(() -> new CouponNotFoundException(couponId));
    }

    // 모든 쿠폰 조회
    @Transactional(readOnly = true)
    public Page<CouponResponse> getAllCoupons(Pageable pageable) {
        return couponRepository.findAllByDeletedFalse(pageable)
                .map(CouponResponse::from);
    }

    // 특정 정책 내 쿠폰들 모두 삭제
    public void deleteCouponsByPolicy(Long policyId) {
        List<Coupon> coupons = couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(policyId);
        for (Coupon coupon : coupons) {
            coupon.delete();
        }
        couponRepository.saveAll(coupons);
    }
}

