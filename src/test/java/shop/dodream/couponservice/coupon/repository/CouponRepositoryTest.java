package shop.dodream.couponservice.coupon.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private shop.dodream.couponservice.policy.repository.CouponPolicyRepository couponPolicyRepository;

    private CouponPolicy savePolicy() {
        CouponPolicy policy = CouponPolicy.builder()
                .name("policy")
                .discountType(DiscountType.FLAT)
                .discountValue(1000L)
                .minPurchaseAmount(0L)
                .maxDiscountAmount(2000L)
                .expiredStrategy(ExpiredStrategy.FIXED)
                .fixedDate(ZonedDateTime.now().plusDays(10))
                .build();
        return couponPolicyRepository.save(policy);
    }

    @Test
    @DisplayName("delete 플래그가 false인 쿠폰만 조회한다")
    void findByDeletedFalse() {
        CouponPolicy policy = savePolicy();
        Coupon c1 = Coupon.builder().couponPolicy(policy).bookId(1L).build();
        Coupon c2 = Coupon.builder().couponPolicy(policy).bookId(2L).deleted(true).build();
        couponRepository.saveAll(List.of(c1, c2));

        List<Coupon> result = couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(policy.getPolicyId());

        assertThat(result).hasSize(1).extracting(Coupon::getBookId).containsExactly(1L);
        assertThat(couponRepository.findAllByDeletedFalse(PageRequest.of(0, 10)).getContent()).hasSize(1);
        Optional<Coupon> found = couponRepository.findByCouponIdAndDeletedFalse(c1.getCouponId());
        assertThat(found).isPresent();
    }
}