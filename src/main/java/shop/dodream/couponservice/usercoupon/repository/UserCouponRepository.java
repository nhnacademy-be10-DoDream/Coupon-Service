package shop.dodream.couponservice.usercoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, AvailableCouponRepository {

    @Modifying(clearAutomatically = true)
    @Query("update UserCoupon uc set uc.status = shop.dodream.couponservice.common.CouponStatus.APPLIED " +
            "where uc.userCouponId in :userCouponIds and uc.userId = :userId and uc.status = shop.dodream.couponservice.common.CouponStatus.AVAILABLE")
    int applyAllByIds(@Param("userCouponIds") List<Long> userCouponIds, @Param("userId") String userId);

    @Modifying(clearAutomatically = true)
    @Query("update UserCoupon uc set uc.status = shop.dodream.couponservice.common.CouponStatus.USED, uc.usedAt = :usedAt " +
            "where uc.userCouponId in :userCouponIds and uc.userId = :userId and uc.status = shop.dodream.couponservice.common.CouponStatus.APPLIED")
    int useAllByIds(@Param("userCouponIds") List<Long> userCouponIds, @Param("userId") String userId, @Param("usedAt") ZonedDateTime usedAt);

    @Modifying(clearAutomatically = true)
    @Query("update UserCoupon uc set uc.status = shop.dodream.couponservice.common.CouponStatus.AVAILABLE " +
            "where uc.userCouponId in :userCouponIds and uc.userId = :userId and uc.deleted = false")
    int revokeAllByIds(@Param("userCouponIds") List<Long> userCouponIds, @Param("userId") String userId);

    List<UserCoupon> findByCoupon_CouponIdAndDeletedFalse(Long couponId);

    Optional<UserCoupon> findByUserCouponIdAndDeletedFalse(Long userCouponId);


}
