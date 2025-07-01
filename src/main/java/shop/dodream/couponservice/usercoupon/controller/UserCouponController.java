package shop.dodream.couponservice.usercoupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.couponservice.common.annotation.CurrentUser;
import shop.dodream.couponservice.usercoupon.dto.AvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.BookAvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
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

    @GetMapping("/me/coupons/available")
    public ResponseEntity<List<AvailableCouponResponse>> getAvailableCoupons(@CurrentUser String userId) {
        return ResponseEntity.ok(userCouponService.getAvailableCoupons(userId));
    }

    @PutMapping("/me/coupons/{user-coupon-id}/use")
    public ResponseEntity<Void> useCoupon(
            @CurrentUser String userId,
            @PathVariable("user-coupon-id") Long userCouponId
    ) {
        userCouponService.useCoupon(userId, userCouponId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coupons/book/{book-id}")
    public ResponseEntity<List<BookAvailableCouponResponse>> getAvailableCouponsforBook(
            @PathVariable("book-id") Long bookId,
            @CurrentUser String userId,
            @RequestParam Long bookPrice) {

        List<BookAvailableCouponResponse> coupons = userCouponService.getBookAvailableCoupons(userId, bookId, bookPrice);

        return ResponseEntity.ok(coupons);
    }




}
