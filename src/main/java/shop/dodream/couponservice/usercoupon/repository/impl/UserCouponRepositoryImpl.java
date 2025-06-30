package shop.dodream.couponservice.usercoupon.repository.impl;

import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.coupon.entity.QCoupon;
import shop.dodream.couponservice.policy.entity.QCouponPolicy;
import shop.dodream.couponservice.usercoupon.dto.AvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.entity.QUserCoupon;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.repository.AvailableCouponRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Transactional(readOnly = true)
public class UserCouponRepositoryImpl extends QuerydslRepositorySupport implements AvailableCouponRepository {

    public UserCouponRepositoryImpl() {
        super(UserCoupon.class);
    }


    @Override
    public List<AvailableCouponResponse> findAllAvailableByUserId(String userId) {

        QUserCoupon uc = QUserCoupon.userCoupon;
        QCoupon c = QCoupon.coupon;
        QCouponPolicy cp = QCouponPolicy.couponPolicy;

        return from(uc)
                .join(uc.coupon, c)
                .join(c.couponPolicy, cp)
                .where(
                        uc.userId.eq(userId),
                        uc.usedAt.isNull(),
                        uc.expiredAt.after(ZonedDateTime.now())
                )
                .select(Projections.constructor(AvailableCouponResponse.class,
                        cp.name.as("policyName"),
                        cp.discountType.as("discountType"),
                        cp.discountValue.as("discountValue"),
                        cp.minPurchaseAmount.as("minPurchaseAmount"),
                        cp.maxDiscountAmount.as("maxDiscountAmount"),
                        uc.issuedAt,
                        uc.expiredAt
                ))
                .fetch();
    }
}
