package shop.dodream.couponservice.usercoupon.repository.impl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.common.CouponStatus;
import shop.dodream.couponservice.coupon.entity.QCoupon;
import shop.dodream.couponservice.policy.entity.QCouponPolicy;
import shop.dodream.couponservice.usercoupon.dto.*;
import shop.dodream.couponservice.usercoupon.entity.QUserCoupon;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.repository.AvailableCouponRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Transactional(readOnly = true)
public class AvailableCouponRepositoryImpl extends QuerydslRepositorySupport implements AvailableCouponRepository {

    public AvailableCouponRepositoryImpl() {
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
                        uc.expiredAt.after(ZonedDateTime.now()),
                        uc.deleted.isFalse()
                )
                .select(new QAvailableCouponResponse(
                        cp.name,
                        cp.discountType,
                        cp.discountValue,
                        cp.minPurchaseAmount,
                        cp.maxDiscountAmount,
                        uc.issuedAt,
                        uc.expiredAt
                ))
                .fetch();
    }

    @Override
    public List<BookAvailableCouponResponse> findAvailableCouponsForBook(String userId, Long bookId, List<Long> categoryIds, Long bookPrice) {
        QUserCoupon uc = QUserCoupon.userCoupon;
        QCoupon c = QCoupon.coupon;
        QCouponPolicy cp = QCouponPolicy.couponPolicy;

        return from(uc)
                .join(uc.coupon, c)
                .join(c.couponPolicy, cp)
                .where(
                        uc.userId.eq(userId),
                        uc.usedAt.isNull(),
                        uc.expiredAt.after(ZonedDateTime.now()),
                        uc.status.eq(CouponStatus.AVAILABLE),
                        uc.deleted.isFalse(),
                        cp.minPurchaseAmount.loe(bookPrice),
                        c.bookId.eq(bookId)
                                .or(c.categoryId.in(categoryIds))
                                .or(c.bookId.isNull().and(c.categoryId.isNull()))

                )
                .select(new QBookAvailableCouponResponse(
                        uc.userCouponId,
                        cp.name,
                        cp.discountValue,
                        cp.minPurchaseAmount,
                        cp.maxDiscountAmount
                ))
                .fetch();
    }

    @Override
    public List<OrderAppliedCouponResponse> findAppliedCouponsForOrder(String userId, Long bookId, List<Long> categoryIds, Long bookPrice, Long userCouponId) {

        QUserCoupon uc = QUserCoupon.userCoupon;
        QCoupon c = QCoupon.coupon;
        QCouponPolicy cp = QCouponPolicy.couponPolicy;

        return from(uc)
                .join(uc.coupon, c)
                .join(c.couponPolicy, cp)
                .where(
                        uc.userId.eq(userId),
                        uc.usedAt.isNull(),
                        uc.userCouponId.eq(userCouponId),
                        uc.expiredAt.after(ZonedDateTime.now()),
                        uc.status.eq(CouponStatus.APPLIED),
                        uc.deleted.isFalse(),
                        cp.minPurchaseAmount.loe(bookPrice),
                        c.bookId.eq(bookId)
                                .or(c.categoryId.in(categoryIds))
                                .or(c.bookId.isNull().and(c.categoryId.isNull()))

                )
                .select(new QOrderAppliedCouponResponse(
                        uc.userCouponId,
                        cp.name,
                        cp.discountValue,
                        cp.minPurchaseAmount,
                        cp.maxDiscountAmount
                ))
                .fetch();
    }

}
