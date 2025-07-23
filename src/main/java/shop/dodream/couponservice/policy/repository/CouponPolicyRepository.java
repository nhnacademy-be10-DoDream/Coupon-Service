package shop.dodream.couponservice.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

import java.util.List;
import java.util.Optional;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    boolean existsByName(String name);

    Optional<CouponPolicy> findByPolicyIdAndDeletedFalse(Long policyId);

    boolean existsByPolicyIdAndDeletedFalse(Long policyId);

    List<CouponPolicy> findAllByDeletedFalse();

    Optional<CouponPolicy> findByNameContainsAndDeletedFalse(String name);
}
