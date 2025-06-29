package shop.dodream.couponservice.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.dodream.couponservice.coupon.entity.Coupon;
import java.util.Collection;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByCouponPolicyPolicyId(Long policyId);

}
