package shop.dodream.couponservice.usercoupon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import shop.dodream.couponservice.common.CouponStatus;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.common.properties.CouponRabbitProperties;
import shop.dodream.couponservice.common.rabbitmq.IssueWelcomeRabbitListener;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.UnauthorizedUserCouponAccessException;
import shop.dodream.couponservice.exception.InvalidUserCouponStatusException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import shop.dodream.couponservice.usercoupon.controller.BookServiceClient;
import shop.dodream.couponservice.usercoupon.controller.UserServiceClient;
import shop.dodream.couponservice.usercoupon.dto.*;
import shop.dodream.couponservice.usercoupon.entity.UserCoupon;
import shop.dodream.couponservice.usercoupon.repository.UserCouponRepository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class UserCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private BookServiceClient bookServiceClient;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private CouponRabbitProperties properties;
    @Mock
    private CouponPolicyRepository couponPolicyRepository;
    @Mock
    private IssueWelcomeRabbitListener issueWelcomeRabbitListener;

    @InjectMocks
    private UserCouponService userCouponService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private Coupon coupon() {
        CouponPolicy policy = CouponPolicy.builder()
                .name("p")
                .discountType(DiscountType.FLAT)
                .discountValue(1000L)
                .minPurchaseAmount(0L)
                .maxDiscountAmount(2000L)
                .expiredStrategy(ExpiredStrategy.FIXED)
                .fixedDate(ZonedDateTime.now().plusDays(1))
                .build();
        return Coupon.builder().couponId(1L).couponPolicy(policy).build();
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon() {
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(coupon()));
        IssueCouponRequest req = new IssueCouponRequest("u", 1L);
        userCouponService.issuedCoupon(req);
        verify(userCouponRepository).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("쿠폰 발급 실패")
    void issueCouponFail() {
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.empty());
        IssueCouponRequest req = new IssueCouponRequest("u", 1L);
        assertThatThrownBy(() -> userCouponService.issuedCoupon(req))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    @DisplayName("권한 없는 사용자는 revoke 불가")
    void revokeFail() {
        UserCoupon uc = UserCoupon.builder()
                .userCouponId(1L)
                .userId("u1")
                .coupon(coupon())
                .issuedAt(ZonedDateTime.now())
                .expiredAt(ZonedDateTime.now().plusDays(1))
                .status(CouponStatus.AVAILABLE)
                .build();
        given(userCouponRepository.findByUserCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(uc));
        assertThatThrownBy(() -> userCouponService.revokeCoupon("u2", 1L))
                .isInstanceOf(UnauthorizedUserCouponAccessException.class);
    }

    @Test
    @DisplayName("revoke 성공")
    void revokeSuccess() {
        UserCoupon uc = UserCoupon.builder()
                .userCouponId(1L)
                .userId("u")
                .coupon(coupon())
                .issuedAt(ZonedDateTime.now())
                .expiredAt(ZonedDateTime.now().plusDays(1))
                .status(CouponStatus.APPLIED)
                .build();
        given(userCouponRepository.findByUserCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(uc));
        userCouponService.revokeCoupon("u", 1L);
        verify(userCouponRepository).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("applyCoupons 성공")
    void applyCoupons() {
        given(userCouponRepository.applyAllByIds(List.of(1L), "u")).willReturn(1);
        BookCouponRequest br = new BookCouponRequest(1L, 1L);
        TransactionSynchronizationManager.initSynchronization();
        try {
            userCouponService.applyCoupons("u", List.of(br));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
        verify(userCouponRepository).applyAllByIds(List.of(1L), "u");
    }

    @Test
    @DisplayName("applyCoupons 실패")
    void applyCouponsFail() {
        given(userCouponRepository.applyAllByIds(List.of(1L), "u")).willReturn(0);
        BookCouponRequest br = new BookCouponRequest(1L, 1L);
        TransactionSynchronizationManager.initSynchronization();
        try {
            assertThatThrownBy(() -> userCouponService.applyCoupons("u", List.of(br)))
                    .isInstanceOf(InvalidUserCouponStatusException.class);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("다수 사용자에게 쿠폰 발급")
    void issueCouponsToUsers() {
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(coupon()));
        var req = new shop.dodream.couponservice.usercoupon.dto.IssueCouponToUsersRequest(1L, List.of("u1", "u2"), null);
        userCouponService.issueCouponsToUsers(req);
        verify(userCouponRepository).saveAll(any());
    }

    @Test
    @DisplayName("조건으로 쿠폰 발급시 사용자를 조회한다")
    void issueCouponsToUsersByCondition() {
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(coupon()));
        given(userServiceClient.getUsers(any(), any())).willReturn(List.of("u1"));
        var cond = new shop.dodream.couponservice.usercoupon.dto.UserSearchCondition();
        var req = new shop.dodream.couponservice.usercoupon.dto.IssueCouponToUsersRequest(1L, null, cond);
        userCouponService.issueCouponsToUsers(req);
        verify(userServiceClient).getUsers(any(), any());
        verify(userCouponRepository).saveAll(any());
    }

    @Test
    @DisplayName("조건 발급시 사용자 없으면 예외")
    void issueCouponsToUsersFail() {
        given(couponRepository.findByCouponIdAndDeletedFalse(1L)).willReturn(Optional.of(coupon()));
        given(userServiceClient.getUsers(any(), any())).willReturn(List.of());
        var cond = new shop.dodream.couponservice.usercoupon.dto.UserSearchCondition();
        var req = new shop.dodream.couponservice.usercoupon.dto.IssueCouponToUsersRequest(1L, null, cond);
        assertThatThrownBy(() -> userCouponService.issueCouponsToUsers(req))
                .isInstanceOf(shop.dodream.couponservice.exception.UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용가능한 쿠폰 조회 호출")
    void getAvailableCoupons() {
        given(userCouponRepository.findAllAvailableByUserId("u")).willReturn(List.of());
        userCouponService.getAvailableCoupons("u");
        verify(userCouponRepository).findAllAvailableByUserId("u");
    }

    @Test
    @DisplayName("특정 쿠폰으로 발급된 유저 쿠폰 조회")
    void getUserCouponsByCoupon() {
        UserCoupon uc = UserCoupon.builder()
                .userCouponId(1L)
                .userId("u")
                .coupon(coupon())
                .issuedAt(ZonedDateTime.now())
                .expiredAt(ZonedDateTime.now().plusDays(1))
                .status(CouponStatus.AVAILABLE)
                .build();
        given(userCouponRepository.findByCoupon_CouponIdAndDeletedFalse(1L)).willReturn(List.of(uc));
        var result = userCouponService.getUserCouponsByCoupon(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("특정 쿠폰으로 발급된 유저 쿠폰 삭제")
    void deleteUserCouponsByCoupon() {
        UserCoupon uc = UserCoupon.builder()
                .userCouponId(1L)
                .userId("u")
                .coupon(coupon())
                .issuedAt(ZonedDateTime.now())
                .expiredAt(ZonedDateTime.now().plusDays(1))
                .status(CouponStatus.AVAILABLE)
                .build();
        given(userCouponRepository.findByCoupon_CouponIdAndDeletedFalse(1L)).willReturn(List.of(uc));
        userCouponService.deleteUserCouponsByCoupon(1L);
        verify(userCouponRepository).saveAll(any());
    }

    private BookDetailResponse detail(long price) throws Exception {
        BookDetailResponse res = new BookDetailResponse();
        var f = BookDetailResponse.class.getDeclaredField("salePrice");
        f.setAccessible(true);
        f.set(res, price);
        return res;
    }

    @Test
    @DisplayName("카테고리 트리 조회 후 ID 목록 반환")
    void getCategoryIdsByBook() {
        CategoryTreeResponse child = new CategoryTreeResponse(2L, "c2", 1L, 1L, List.of());
        CategoryTreeResponse root = new CategoryTreeResponse(1L, "c1", 0L, null, List.of(child));
        given(bookServiceClient.getCategoriesByBookId(1L)).willReturn(List.of(root));

        List<Long> ids = userCouponService.getCategoryIdsByBook(1L);

        assertThat(ids).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("도서 사용가능 쿠폰 조회 시 최종 가격 계산")
    void getBookAvailableCoupons() throws Exception {
        CategoryTreeResponse cat = new CategoryTreeResponse(1L, "c", 0L, null, List.of());
        given(bookServiceClient.getCategoriesByBookId(1L)).willReturn(List.of(cat));
        given(bookServiceClient.getBookSalePrice(1L)).willReturn(detail(20000L));
        BookAvailableCouponResponse dto = new BookAvailableCouponResponse(1L, "p", 10L, 0L, null);
        given(userCouponRepository.findAvailableCouponsForBook("u", 1L, List.of(1L), 20000L))
                .willReturn(List.of(dto));

        List<BookAvailableCouponResponse> result = userCouponService.getBookAvailableCoupons("u", 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFinalPrice()).isEqualTo(18000L);
    }

    @Test
    @DisplayName("주문 적용 쿠폰 조회 시 최종 가격 계산")
    void getOrderAppliedCoupons() {
        CategoryTreeResponse cat = new CategoryTreeResponse(1L, "c", 0L, null, List.of());
        given(bookServiceClient.getCategoriesByBookId(1L)).willReturn(List.of(cat));
        OrderAppliedCouponResponse repoRes = new OrderAppliedCouponResponse(1L, "p", 10L, 0L, null);
        given(userCouponRepository.findAppliedCouponsForOrder("u", 1L, List.of(1L), 20000L, 1L))
                .willReturn(List.of(repoRes));
        OrderAppliedCouponRequest req = new OrderAppliedCouponRequest("u",
                List.of(new BookPriceRequest(1L, 20000L, 1L)));

        List<OrderAppliedCouponResponse> result = userCouponService.getOrderAppliedCoupons(req);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookId()).isEqualTo(1L);
        assertThat(result.get(0).getFinalPrice()).isEqualTo(18000L);
    }

    @Test
    @DisplayName("USED 상태 쿠폰 복구")
    void revokeUsedCoupons() {
        given(userCouponRepository.revokeAllUsedByIds(List.of(1L), "u")).willReturn(1);
        userCouponService.revokeUsedCoupons("u", List.of(1L));
        verify(userCouponRepository).revokeAllUsedByIds(List.of(1L), "u");
    }

    @Test
    @DisplayName("USED 상태 쿠폰 복구 실패")
    void revokeUsedCouponsFail() {
        given(userCouponRepository.revokeAllUsedByIds(List.of(1L), "u")).willReturn(0);
        assertThatThrownBy(() -> userCouponService.revokeUsedCoupons("u", List.of(1L)))
                .isInstanceOf(InvalidUserCouponStatusException.class);
    }

    @Test
    @DisplayName("APPLIED 상태 쿠폰 복구")
    void revokeAppliedCoupons() {
        ReleasePayload payload = new ReleasePayload("u", List.of(1L));
        given(userCouponRepository.revokeAllAppliedByIds(List.of(1L), "u")).willReturn(1);
        userCouponService.revokeAppliedCoupons(payload);
        verify(userCouponRepository).revokeAllAppliedByIds(List.of(1L), "u");
    }

    @Test
    @DisplayName("APPLIED 상태 쿠폰 복구 개수 불일치")
    void revokeAppliedCouponsMismatch() {
        ReleasePayload payload = new ReleasePayload("u", List.of(1L,2L));
        given(userCouponRepository.revokeAllAppliedByIds(List.of(1L,2L), "u")).willReturn(1);
        assertThatThrownBy(() -> userCouponService.revokeAppliedCoupons(payload))
                .isInstanceOf(InvalidUserCouponStatusException.class);
    }

    @Test
    @DisplayName("APPLIED 상태 쿠폰 복구 대상 없음")
    void revokeAppliedCouponsNone() {
        ReleasePayload payload = new ReleasePayload("u", List.of(1L));
        given(userCouponRepository.revokeAllAppliedByIds(List.of(1L), "u")).willReturn(0);
        userCouponService.revokeAppliedCoupons(payload);
        verify(userCouponRepository).revokeAllAppliedByIds(List.of(1L), "u");
    }

    @Test
    @DisplayName("useCoupons 성공")
    void useCoupons() {
        given(userCouponRepository.useAllByIds(
                eq(List.of(1L)),
                eq("u"),
                any(ZonedDateTime.class)
        )).willReturn(1);

        userCouponService.useCoupons("u", List.of(1L));

        verify(userCouponRepository).useAllByIds(
                eq(List.of(1L)), eq("u"), any(ZonedDateTime.class));

    }

    @Test
    @DisplayName("useCoupons 실패")
    void useCouponsFail() {
        given(userCouponRepository.useAllByIds(
                eq(List.of(1L)), eq("u"), any(ZonedDateTime.class)
        )).willReturn(0);

        assertThatThrownBy(() ->
                userCouponService.useCoupons("u", List.of(1L)))
                .isInstanceOf(InvalidUserCouponStatusException.class);
    }

}