package shop.dodream.couponservice.coupon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.dodream.couponservice.coupon.entity.Coupon;
import java.util.Collection;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByCouponPolicyPolicyIdAndDeletedFalse(Long policyId);

    Page<Coupon> findAllByDeletedFalse(Pageable pageable);

    boolean existsByCouponIdAndDeletedFalse(Long couponId);

    java.util.Optional<Coupon> findByCouponIdAndDeletedFalse(Long couponId);

}
