package shop.dodream.couponservice.usercoupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import shop.dodream.couponservice.common.CouponStatus;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.common.properties.CouponRabbitProperties;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.*;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import shop.dodream.couponservice.usercoupon.controller.BookServiceClient;
import shop.dodream.couponservice.usercoupon.controller.UserServiceClient;
import shop.dodream.couponservice.usercoupon.dto.*;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.repository.UserCouponRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final BookServiceClient bookServiceClient;
    private final UserServiceClient userServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final CouponRabbitProperties properties;

    // 유저에 쿠폰 발급
    @Transactional
    public void issuedCoupon(IssueCouponRequest request) {
        Coupon coupon = couponRepository.findByCouponIdAndDeletedFalse(request.getCouponId())
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

    // 조건부로 유저들에게 쿠폰 발급
    @Transactional
    public void issueCouponsToUsers(IssueCouponToUsersRequest request) {
        Coupon coupon = couponRepository.findByCouponIdAndDeletedFalse(request.getCouponId())
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

        List<String> userIds = request.getUserIds();

        if (userIds == null || userIds.isEmpty() && request.getCondition() != null) {
            userIds = userServiceClient.getUsers(request.getCondition().getGrade(), request.getCondition().getBirthMonth());
        }

        if (userIds == null || userIds.isEmpty()) {
            throw new UserNotFoundException("users not found");
        }

        List<UserCoupon> userCoupons = userIds.stream()
                .map(userId -> UserCoupon.builder()
                        .userId(userId)
                        .coupon(coupon)
                        .issuedAt(issuedAt)
                        .expiredAt(expiredAt)
                        .status(CouponStatus.AVAILABLE)
                        .build())
                .toList();

        userCouponRepository.saveAll(userCoupons);
    }

    // 사용자 마이페이지 사용가능한 전체 쿠폰 조회 페이징?
    @Transactional(readOnly = true)
    public List<AvailableCouponResponse> getAvailableCoupons(String userId) {
        return userCouponRepository.findAllAvailableByUserId(userId);
    }

    // 상품별 사용가능한 쿠폰들 - 장바구니 적용?
    @Transactional(readOnly = true)
    public List<BookAvailableCouponResponse> getBookAvailableCoupons(String userId, Long bookId) {
        List<Long> categoryIds = getCategoryIdsByBook(bookId);
        Long bookPrice = bookServiceClient.getBookSalePrice(bookId).getSalePrice();
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

    @Transactional
    public void applyCoupons(String userId, List<BookCouponRequest> requests) {

        List<Long> userCouponIds = requests.stream()
                .map(BookCouponRequest::getUserCouponId)
                .toList();

        int updated = userCouponRepository.applyAllByIds(userCouponIds, userId);
        if (updated != userCouponIds.size()) {
            throw new InvalidUserCouponStatusException("InvalidUserCouponStatus");
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        ReleasePayload payload = new ReleasePayload(userId, userCouponIds);
                        rabbitTemplate.convertAndSend(
                                "",
                                properties.getDelayQueue(),
                                payload,
                                msg -> {
                                    msg.getMessageProperties()
                                            .setExpiration(String.valueOf(30 * 60 * 1000));
                                    return msg;
                                }
                        );
                    }
                }
        );
    }

    @Transactional
    public void revokeCoupon(String userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findByUserCouponIdAndDeletedFalse(userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException(userCouponId));
        if (!userCoupon.getUserId().equals(userId)) {
            throw new UnauthorizedUserCouponAccessException(userId, userCouponId);
        }
        userCoupon.revoke();
        userCouponRepository.save(userCoupon);
    }

    @RabbitListener(queues = "${coupon.rabbit.releaseQueue}" , containerFactory = "rabbitListenerContainerFactory")
    @Transactional
    public void revokeAppliedCoupons(ReleasePayload payload) {
        String userId = payload.getUserId();
        List<Long> userCouponIds = payload.getUserCouponIds();
        int revokeCount = userCouponRepository.revokeAllAppliedByIds(userCouponIds, userId);
        if (revokeCount == 0) {
            log.info("No coupons to revoke for user={} ids={}",userId ,userCouponIds);
        } else if (revokeCount != userCouponIds.size()) {
            throw new InvalidUserCouponStatusException("InvalidUserCouponStatus");
        }
    }

    @Transactional
    public void revokeUsedCoupons(String userId, List<Long> userCouponIds) {
        if (userId == null) {
            throw new UserNotFoundException("user is null");
        }
        int revokeCount = userCouponRepository.revokeAllUsedByIds(userCouponIds, userId);
        if (revokeCount != userCouponIds.size()) {
            throw new InvalidUserCouponStatusException("InvalidUserCouponStatus");
        }
    }

    @Transactional
    public void useCoupons(String userId, List<Long> userCouponIds) {
        if (userId == null) {
            throw new UserNotFoundException("user is null");
        }

        if (userCouponIds == null || userCouponIds.isEmpty()) {
            return;
        }

        int updatedCount = userCouponRepository.useAllByIds(userCouponIds, userId, ZonedDateTime.now());

        if (updatedCount != userCouponIds.size()) {
            throw new InvalidUserCouponStatusException("InvalidUserCouponStatus");
        }
    }

    @Transactional(readOnly = true)
    public List<OrderAppliedCouponResponse> getOrderAppliedCoupons(OrderAppliedCouponRequest requests) {
        List<OrderAppliedCouponResponse> appliedresponses = new ArrayList<>();
        String userId = requests.getUserId();
        for (BookPriceRequest request : requests.getBooks()) {
            List<Long> categories = Optional.ofNullable(getCategoryIdsByBook(request.getBookId())).orElse(new ArrayList<>());
            List<OrderAppliedCouponResponse> responses = userCouponRepository.findAppliedCouponsForOrder(userId,
                    request.getBookId(),
                    categories,
                    request.getBookPrice(),
                    request.getCouponId());

            for (OrderAppliedCouponResponse response : responses) {

                response.setBookId(request.getBookId());

                Long discountValue = response.getDiscountValue();
                Long bookPrice = request.getBookPrice();
                Long discountAmount;

                if (discountValue <= 100) {
                    discountAmount = Math.round(bookPrice * (discountValue / 100.0));
                } else {
                    discountAmount = discountValue;
                }

                Long maxDiscount = response.getMaxDiscountAmount();
                if (maxDiscount != null) {
                    discountAmount = Math.min(maxDiscount, discountAmount);
                }

                Long finalPrice = bookPrice - discountAmount;
                response.setFinalPrice(finalPrice);
            }
            appliedresponses.addAll(responses);
        }
        return  appliedresponses;
    }

    @Transactional(readOnly = true)
    public List<Long> getCategoryIdsByBook(Long bookId) {
        List<CategoryTreeResponse> categories = bookServiceClient.getCategoriesByBookId(bookId);
        return collectAllCategoryIds(categories);
    }

    @Transactional(readOnly = true)
    public List<UserCouponResponse> getUserCouponsByCoupon(Long couponId) {
        return userCouponRepository.findByCoupon_CouponIdAndDeletedFalse(couponId).stream()
                .map(UserCouponResponse::from)
                .toList();
    }

    @Transactional
    public void deleteUserCouponsByCoupon(Long couponId) {
        List<UserCoupon> coupons = userCouponRepository.findByCoupon_CouponIdAndDeletedFalse(couponId);
        for (UserCoupon uc : coupons) {
            uc.delete();
        }
        userCouponRepository.saveAll(coupons);
    }

    // 카테고리들
    private List<Long> collectAllCategoryIds(List<CategoryTreeResponse> categories) {
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
