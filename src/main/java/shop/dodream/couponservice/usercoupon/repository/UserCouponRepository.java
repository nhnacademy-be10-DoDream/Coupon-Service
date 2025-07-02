package shop.dodream.couponservice.usercoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, AvailableCouponRepository {

}
