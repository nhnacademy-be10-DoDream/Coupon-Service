package shop.dodream.couponservice.usercoupon.controller;

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
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping("/admin/user-coupons")
    public ResponseEntity<Void> issueCoupon(@RequestBody @Valid IssueCouponRequest request) {
        userCouponService.issuedCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/admin/user-coupons/issue_multiple")
    public ResponseEntity<Void> issueCouponsToUsers(@RequestBody @Valid IssueCouponToUsersRequest request) {
        userCouponService.issueCouponsToUsers(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/coupons/me/available")
    public ResponseEntity<List<AvailableCouponResponse>> getAvailableCoupons(@CurrentUser String userId) {
        return ResponseEntity.ok(userCouponService.getAvailableCoupons(userId));
    }

    @PutMapping("/coupons/me/apply-multiple")
    public ResponseEntity<Void> applyCoupons(
            @CurrentUser String userId,
            @RequestBody @Valid ApplyCouponsRequest request
    ) {
        userCouponService.applyCoupons(userId, request.getRequests());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/coupons/me/{user-coupon-id}/revoke")
    public ResponseEntity<Void> revokeCoupon(
            @CurrentUser String userId,
            @PathVariable("user-coupon-id") Long userCouponId
    ) {
        userCouponService.revokeCoupon(userId, userCouponId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/coupons/me/use-multiple")
    public ResponseEntity<Void> useMultipleCoupons(
            @CurrentUser String userId,
            @RequestBody @Valid UseCouponsRequest request
    ) {
        userCouponService.useCoupons(userId, request.getUserCouponIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coupons/book/{book-id}")
    public ResponseEntity<List<BookAvailableCouponResponse>> getAvailableCouponsforBook(
            @PathVariable("book-id") Long bookId,
            @CurrentUser String userId) {

        List<BookAvailableCouponResponse> coupons = userCouponService.getBookAvailableCoupons(userId, bookId);

        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/coupons/me/order-vaild")
    public ResponseEntity<List<OrderAppliedCouponResponse>> orderCouponsVaild(
            @CurrentUser String userId,
            @RequestBody @Valid List<OrderAppliedCouponRequest> requests) {

        List<OrderAppliedCouponResponse> coupons = userCouponService.getOrderAppliedCoupons(userId, requests);
        
        return ResponseEntity.ok(coupons);
    }
}
