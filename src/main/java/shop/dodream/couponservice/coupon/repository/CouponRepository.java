package shop.dodream.couponservice.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.dodream.couponservice.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
