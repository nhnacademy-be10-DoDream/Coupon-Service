package shop.dodream.couponservice.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.dto.CreateCouponRequest;
import shop.dodream.couponservice.coupon.service.CouponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<Void> createCoupon(@RequestBody @Valid CreateCouponRequest request) {
        couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{coupon-id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable("coupon-id") Long couponId) {
        CouponResponse response = couponService.getCoupon(couponId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{coupon-id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable("coupon-id") Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

}
