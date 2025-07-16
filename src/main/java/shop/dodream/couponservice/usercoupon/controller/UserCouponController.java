package shop.dodream.couponservice.usercoupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.couponservice.common.annotation.CurrentUser;
import shop.dodream.couponservice.usercoupon.dto.*;
import shop.dodream.couponservice.usercoupon.service.UserCouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Coupon", description = "유저 쿠폰 관련 API")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @Operation(summary = "관리자 유저 쿠폰 단일 발급", description = "유저 쿠폰 단일 발급 API")
    @PostMapping("/admin/user-coupons")
    public ResponseEntity<Void> issueCoupon(@RequestBody @Valid IssueCouponRequest request) {
        userCouponService.issuedCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "관리자 유저 쿠폰 다인 발급", description = "유저 쿠폰 여러명 발급 API")
    @PostMapping("/admin/user-coupons/issue_multiple")
    public ResponseEntity<Void> issueCouponsToUsers(@RequestBody @Valid IssueCouponToUsersRequest request) {
        userCouponService.issueCouponsToUsers(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "유저 사용가능한 모든 쿠폰 조회", description = "사용자 사용가능한 모든 쿠폰 조회 API")
    @GetMapping("/coupons/me/available")
    public ResponseEntity<List<AvailableCouponResponse>> getAvailableCoupons(@CurrentUser String userId) {
        return ResponseEntity.ok(userCouponService.getAvailableCoupons(userId));
    }

    @Operation(summary = "다수의 유저 쿠폰 상태 APPLIED 변경", description = "다수의 유저 쿠폰 상태 APPLIED 변경 API")
    @PutMapping("/coupons/me/apply-multiple")
    public ResponseEntity<Void> applyCoupons(
            @CurrentUser String userId,
            @RequestBody @Valid ApplyCouponsRequest request
    ) {
        userCouponService.applyCoupons(userId, request.getRequests());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "단일 유저쿠폰 상태 복구", description = "단일 유저쿠폰 상태 AVAILABLE 복구 API")
    @PutMapping("/coupons/me/{user-coupon-id}/revoke")
    public ResponseEntity<Void> revokeCoupon(
            @CurrentUser String userId,
            @PathVariable("user-coupon-id") Long userCouponId
    ) {
        userCouponService.revokeCoupon(userId, userCouponId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "여러 유저 쿠폰 사용 처리", description = "여러 유저 쿠폰 사용 처리 API")
    @PutMapping("/coupons/me/use-multiple")
    public ResponseEntity<Void> useMultipleCoupons(
            @CurrentUser String userId,
            @RequestBody @Valid UseCouponsRequest request
    ) {
        userCouponService.useCoupons(userId, request.getUserCouponIds());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 상품에 사용 가능한 유저쿠포들 조회", description = "특정 상품에 사용 가능한 유저쿠포들 조회 API")
    @GetMapping("/coupons/book/{book-id}")
    public ResponseEntity<List<BookAvailableCouponResponse>> getAvailableCouponsforBook(
            @PathVariable("book-id") Long bookId,
            @CurrentUser String userId) {

        List<BookAvailableCouponResponse> coupons = userCouponService.getBookAvailableCoupons(userId, bookId);

        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "실제 적용한 쿠폰이 맞는지 검증", description = "실제 적용한 쿠폰이 맞는지 검증 API")
    @PostMapping("/coupons/me/order-valid")
    public ResponseEntity<List<OrderAppliedCouponResponse>> orderCouponsValid(
            @RequestBody @Valid OrderAppliedCouponRequest requests) {

        List<OrderAppliedCouponResponse> coupons = userCouponService.getOrderAppliedCoupons(requests);

        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "특정 쿠폰으로 발급된 유저 쿠폰 조회", description = "특정 쿠폰으로 발급된 유저 쿠폰 조회 API")
    @GetMapping("/admin/user-coupons/{coupon-id}")
    public ResponseEntity<List<UserCouponResponse>> getUserCouponsByCoupon(@PathVariable("coupon-id") Long couponId) {
        return ResponseEntity.ok(userCouponService.getUserCouponsByCoupon(couponId));
    }

    @Operation(summary = "특정 쿠폰으로 발급된 유저 쿠폰 삭제", description = "특정 쿠폰으로 발급된 유저 쿠폰 논리 삭제 API")
    @DeleteMapping("/admin/user-coupons/{coupon-id}")
    public ResponseEntity<Void> deleteUserCouponsByCoupon(@PathVariable("coupon-id") Long couponId) {
        userCouponService.deleteUserCouponsByCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "유저 쿠폰들 상태 활성화", description = "유저 쿠폰들 상태 활성화 API")
    @PutMapping("/admin/user-coupons/revokes")
    public ResponseEntity<Void> revokeUserCoupons(@CurrentUser String userId,
                                                  @RequestBody List<Long> userCouponIds ) {
        userCouponService.revokeCoupons(userId, userCouponIds);
        return ResponseEntity.noContent().build();
    }
}
