package shop.dodream.couponservice.coupon.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.dto.CreateCouponRequest;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @InjectMocks
    private CouponService couponService;

    CouponServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    private CouponPolicy policy() {
        return CouponPolicy.builder()
                .policyId(1L)
                .name("p")
                .discountType(DiscountType.FLAT)
                .discountValue(1000L)
                .minPurchaseAmount(0L)
                .maxDiscountAmount(2000L)
                .expiredStrategy(ExpiredStrategy.FIXED)
                .fixedDate(ZonedDateTime.now().plusDays(1))
                .build();
    }

    @Test
    @DisplayName("정책이 존재하면 쿠폰을 생성한다")
    void createCoupon() {
        CouponPolicy policy = policy();
        given(couponPolicyRepository.findByPolicyIdAndDeletedFalse(1L)).willReturn(Optional.of(policy));

        CreateCouponRequest request = new CreateCouponRequest(1L, 2L, null);
        couponService.createCoupon(request);

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(captor.capture());
        assertThat(captor.getValue().getBookId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("정책이 존재하지 않으면 예외를 던진다")
    void createCouponFail() {
        given(couponPolicyRepository.findByPolicyIdAndDeletedFalse(1L)).willReturn(Optional.empty());
        CreateCouponRequest request = new CreateCouponRequest(1L, null, null);
        assertThatThrownBy(() -> couponService.createCoupon(request))
                .isInstanceOf(CouponPolicyNotFoundException.class);
    }

    @Test
    @DisplayName("쿠폰을 아이디로 조회한다")
    void getCoupon() {
        CouponPolicy policy = policy();
        Coupon coupon = Coupon.builder().couponId(1L).couponPolicy(policy).build();
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(coupon));

        CouponResponse response = couponService.getCoupon(1L);

        assertThat(response.getCouponId()).isEqualTo(1L);
        assertThat(response.getPolicyName()).isEqualTo("p");
    }

    @Test
    @DisplayName("모든 쿠폰 조회")
    void getAllCoupons() {
        given(couponRepository.findAllByDeletedFalse(any())).willReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        couponService.getAllCoupons(org.springframework.data.domain.PageRequest.of(0, 10));
        verify(couponRepository).findAllByDeletedFalse(any());
    }

    @Test
    @DisplayName("정책별 쿠폰 조회")
    void getCouponsByPolicy() {
        CouponPolicy policy = policy();
        Coupon coupon = Coupon.builder().couponId(1L).couponPolicy(policy).build();
        given(couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(1L)).willReturn(List.of(coupon));

        List<CouponResponse> responses = couponService.getCouponsByPolicy(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCouponId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("정책별 쿠폰 삭제")
    void deleteCouponsByPolicy() {
        Coupon c1 = Coupon.builder().couponId(1L).couponPolicy(policy()).build();
        Coupon c2 = Coupon.builder().couponId(2L).couponPolicy(policy()).build();
        given(couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(1L)).willReturn(List.of(c1, c2));
        couponService.deleteCouponsByPolicy(1L);
        verify(couponRepository).saveAll(any());
    }

    @Test
    @DisplayName("쿠폰 삭제")
    void deleteCoupon() {
        Coupon coupon = Coupon.builder().couponId(1L).couponPolicy(policy()).build();
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(coupon));

        couponService.deleteCoupon(1L);

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("쿠폰 삭제 실패")
    void deleteCouponFail() {
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> couponService.deleteCoupon(1L))
                .isInstanceOf(CouponNotFoundException.class);
    }
}