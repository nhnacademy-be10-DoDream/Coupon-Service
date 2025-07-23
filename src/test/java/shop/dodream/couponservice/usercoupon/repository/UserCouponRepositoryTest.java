package shop.dodream.couponservice.usercoupon.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import shop.dodream.couponservice.common.CouponStatus;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.dto.BookAvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.OrderAppliedCouponResponse;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserCouponRepositoryTest {

    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponPolicyRepository policyRepository;

    private CouponPolicy policy() {
        return policyRepository.save(CouponPolicy.builder()
                .name("p")
                .discountType(DiscountType.FLAT)
                .discountValue(1000L)
                .minPurchaseAmount(0L)
                .maxDiscountAmount(2000L)
                .expiredStrategy(ExpiredStrategy.FIXED)
                .fixedDate(ZonedDateTime.now().plusDays(1))
                .build());
    }

    private UserCoupon createUserCoupon(String userId) {
        Coupon coupon = couponRepository.save(Coupon.builder().couponPolicy(policy()).build());
        UserCoupon uc = UserCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .issuedAt(ZonedDateTime.now())
                .expiredAt(ZonedDateTime.now().plusDays(1))
                .status(CouponStatus.AVAILABLE)
                .build();
        return userCouponRepository.save(uc);
    }

    private UserCoupon createUserCoupon(String userId, CouponPolicy policy,
                                        Long bookId, Long categoryId,
                                        CouponStatus status,
                                        ZonedDateTime expiredAt,
                                        ZonedDateTime usedAt,
                                        boolean deleted) {
        Coupon coupon = couponRepository.save(Coupon.builder()
                .couponPolicy(policy)
                .bookId(bookId)
                .categoryId(categoryId)
                .build());
        UserCoupon uc = UserCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .issuedAt(ZonedDateTime.now())
                .expiredAt(expiredAt)
                .usedAt(usedAt)
                .status(status)
                .deleted(deleted)
                .build();
        return userCouponRepository.save(uc);
    }

    @Test
    @DisplayName("기본 조회 테스트")
    void basicQueries() {
        UserCoupon uc = createUserCoupon("u");
        Optional<UserCoupon> found = userCouponRepository.findByUserCouponIdAndDeletedFalse(uc.getUserCouponId());
        assertThat(found).isPresent();
        List<UserCoupon> byCoupon = userCouponRepository.findByCoupon_CouponIdAndDeletedFalse(uc.getCoupon().getCouponId());
        assertThat(byCoupon).hasSize(1);
    }

    @Test
    @DisplayName("apply, use 쿼리")
    void updateQueries() {
        UserCoupon uc = createUserCoupon("u");
        int count = userCouponRepository.applyAllByIds(List.of(uc.getUserCouponId()), "u");
        assertThat(count).isEqualTo(1);
        uc = userCouponRepository.findById(uc.getUserCouponId()).orElseThrow();
        assertThat(uc.getStatus()).isEqualTo(CouponStatus.APPLIED);

        int used = userCouponRepository.useAllByIds(List.of(uc.getUserCouponId()), "u", ZonedDateTime.now());
        assertThat(used).isEqualTo(1);
        uc = userCouponRepository.findById(uc.getUserCouponId()).orElseThrow();
        assertThat(uc.getStatus()).isEqualTo(CouponStatus.USED);
    }

    @Test
    @DisplayName("사용가능한 쿠폰만 조회")
    void findAllAvailableByUserId() {
        CouponPolicy policy = policy();
        createUserCoupon("u", policy, 1L, null, CouponStatus.AVAILABLE,
                ZonedDateTime.now().plusDays(1), null, false);
        createUserCoupon("u", policy, 1L, null, CouponStatus.USED,
                ZonedDateTime.now().plusDays(1), ZonedDateTime.now(), false);
        createUserCoupon("u", policy, 1L, null, CouponStatus.AVAILABLE,
                ZonedDateTime.now().minusDays(1), null, false);

        List<?> all = userCouponRepository.findAllAvailableByUserId("u");
        assertThat(all).hasSize(1);
    }

    @Test
    @DisplayName("도서에 적용가능한 쿠폰 조회")
    void findAvailableCouponsForBook() {
        CouponPolicy policy = policy();
        UserCoupon uc = createUserCoupon("u", policy, 1L, null, CouponStatus.AVAILABLE,
                ZonedDateTime.now().plusDays(1), null, false);
        createUserCoupon("u", policy, 2L, null, CouponStatus.AVAILABLE,
                ZonedDateTime.now().plusDays(1), null, false);

        List<BookAvailableCouponResponse> result = userCouponRepository.findAvailableCouponsForBook("u", 1L, List.of(), 1000L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCouponId()).isEqualTo(uc.getUserCouponId());
    }

    @Test
    @DisplayName("주문에 적용된 쿠폰 조회")
    void findAppliedCouponsForOrder() {
        CouponPolicy policy = policy();
        UserCoupon uc = createUserCoupon("u", policy, 1L, null, CouponStatus.APPLIED,
                ZonedDateTime.now().plusDays(1), null, false);

        List<OrderAppliedCouponResponse> result = userCouponRepository.findAppliedCouponsForOrder(
                "u", 1L, List.of(), 1000L, uc.getUserCouponId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCouponId()).isEqualTo(uc.getUserCouponId());
    }
}