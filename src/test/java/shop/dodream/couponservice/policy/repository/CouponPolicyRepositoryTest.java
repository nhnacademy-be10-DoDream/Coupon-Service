package shop.dodream.couponservice.policy.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CouponPolicyRepositoryTest {

    @Autowired
    private CouponPolicyRepository repository;

    private CouponPolicy policy(String name) {
        return CouponPolicy.builder()
                .name(name)
                .discountType(DiscountType.FLAT)
                .discountValue(1000L)
                .minPurchaseAmount(0L)
                .maxDiscountAmount(2000L)
                .expiredStrategy(ExpiredStrategy.FIXED)
                .fixedDate(ZonedDateTime.now().plusDays(1))
                .build();
    }

    @Test
    @DisplayName("existsByName 과 deleted false 조회 테스트")
    void basicQueries() {
        CouponPolicy p1 = repository.save(policy("p1"));
        CouponPolicy p2 = repository.save(policy("p2"));
        p2.delete();
        repository.save(p2);

        assertThat(repository.existsByName("p1")).isTrue();
        assertThat(repository.existsByPolicyIdAndDeletedFalse(p1.getPolicyId())).isTrue();
        Optional<CouponPolicy> found = repository.findByPolicyIdAndDeletedFalse(p1.getPolicyId());
        assertThat(found).isPresent();
        List<CouponPolicy> all = repository.findAllByDeletedFalse();
        assertThat(all).hasSize(1);
    }
}