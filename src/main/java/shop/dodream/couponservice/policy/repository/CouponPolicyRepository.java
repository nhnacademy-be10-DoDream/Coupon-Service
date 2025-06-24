package shop.dodream.couponservice.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.dodream.couponservice.policy.entity.CouponPolicy;


public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    boolean existsByName(String name);
}
