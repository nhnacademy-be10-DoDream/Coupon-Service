package shop.dodream.couponservice.usercoupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.common.CouponStatus;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.InvalidCouponPolicyException;
import shop.dodream.couponservice.exception.UnauthorizedUserCouponAccessException;
import shop.dodream.couponservice.exception.UserCouponNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.usercoupon.controller.BookServiceClient;
import shop.dodream.couponservice.usercoupon.dto.AvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.BookAvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.CategoryTreeResponse;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.repository.UserCouponRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final BookServiceClient bookServiceClient;


    public void issuedCoupon(IssueCouponRequest request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException(request.getCouponId()));

        CouponPolicy policy = coupon.getCouponPolicy();

        ZonedDateTime issuedAt = ZonedDateTime.now();
        ZonedDateTime expiredAt;

        if (policy.getExpiredStrategy() == ExpiredStrategy.FIXED) {
            expiredAt = policy.getFixedDate();
        } else if (policy.getExpiredStrategy() == ExpiredStrategy.PLUS) {
            expiredAt = issuedAt.plusDays(policy.getPlusDay());
        } else {
            throw new InvalidCouponPolicyException("유효하지 않은 쿠폰 만료 정책입니다.");
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(request.getUserId())
                .coupon(coupon)
                .issuedAt(ZonedDateTime.now())
                .expiredAt(expiredAt)
                .status(CouponStatus.AVAILABLE)
                .build();

        userCouponRepository.save(userCoupon);
    }

    // 사용자 마이페이지 사용가능한 전체 쿠폰 조회 페이징?
    @Transactional(readOnly = true)
    public List<AvailableCouponResponse> getAvailableCoupons(String userId) {

        List<AvailableCouponResponse> availableCoupons = userCouponRepository.findAllAvailableByUserId(userId);
        return availableCoupons;
    }

    // 상품별 사용가능한 쿠폰들 - 장바구니 적용?
    public List<BookAvailableCouponResponse> getBookAvailableCoupons(String userId, Long bookId, Long bookPrice) {
        List<Long> categoryIds = getCategoryIdsByBook(bookId);
        List<BookAvailableCouponResponse> availableCoupons = userCouponRepository.findAvailableCouponsForBook(userId
                ,bookId
                ,categoryIds
                ,bookPrice);
        for (BookAvailableCouponResponse availableCoupon : availableCoupons) {

            Long discountValue = availableCoupon.getDiscountValue();
            Long discountAmount;

            if (discountValue <= 100) {
                discountAmount = Math.round(bookPrice * (discountValue /100.0));
            } else {
                discountAmount = discountValue;
            }

            if (availableCoupon.getMaxDiscountAmount() != null ) {
                discountAmount = Math.min(availableCoupon.getMaxDiscountAmount(), discountAmount);
            }

            Long finalPrice = bookPrice - discountAmount;

            availableCoupon.setFinalPrice(finalPrice);
        }

        return availableCoupons;
    }


    public void useCoupon(String userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("coupon not found: "+ userCouponId));
        if (!userCoupon.getUserId().equals(userId)) {
            throw new UnauthorizedUserCouponAccessException(userId, userCouponId);
        }

        userCoupon.use();
    }

    public void applyCoupon(String userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("coupon not found: "+ userCouponId));
        if (!userCoupon.getUserId().equals(userId)) {
            throw new UnauthorizedUserCouponAccessException(userId, userCouponId);
        }
        userCoupon.apply();
    }

    public void useCoupons(String userId, List<Long> userCouponIds) {
        List<UserCoupon> userCoupons = userCouponRepository.findAllById(userCouponIds);

        if (userCoupons.size() != userCouponIds.size()) {
            throw new UserCouponNotFoundException("some coupons not found");
        }

        for (UserCoupon userCoupon : userCoupons) {
            if (!userCoupon.getUserId().equals(userId)) {
                throw new UnauthorizedUserCouponAccessException(userId, userCoupon.getUserCouponId());
            }
            userCoupon.use();
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getCategoryIdsByBook(Long bookId) {
        List<CategoryTreeResponse> categories = bookServiceClient.getCategoriesByBookId(bookId);
        return collectAllCategoryIds(categories);
    }

    public List<Long> collectAllCategoryIds(List<CategoryTreeResponse> categories) {
        List<Long> ids = new ArrayList<>();
        for (CategoryTreeResponse category : categories) {
            collectRecursively(category, ids);
        }
        return ids;
    }

    private void collectRecursively(CategoryTreeResponse node, List<Long> collector) {
        if (node == null) return;
        collector.add(node.getCategoryId());
        if (node.getChildren() != null) {
            for (CategoryTreeResponse child : node.getChildren()) {
                collectRecursively(child, collector);
            }
        }
    }
}
