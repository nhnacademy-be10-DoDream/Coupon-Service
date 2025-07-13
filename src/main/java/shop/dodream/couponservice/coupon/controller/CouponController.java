package shop.dodream.couponservice.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.dto.CreateCouponRequest;
import shop.dodream.couponservice.coupon.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
@Tag(name = "Coupon", description = "쿠폰 관련 API")
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "관리자 쿠폰 생성" , description = "관리자 쿠폰 생성 API")
    @PostMapping
    public ResponseEntity<Void> createCoupon(@RequestBody @Valid CreateCouponRequest request) {
        couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "관리자 단일 쿠폰 조회" , description = "관리자 단일 쿠폰 조회 API")
    @GetMapping("/{coupon-id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable("coupon-id") Long couponId) {
        CouponResponse response = couponService.getCoupon(couponId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 모든 쿠폰 조회" , description = "관리자 모든 쿠폰 조회 API")
    @GetMapping
    public ResponseEntity<Page<CouponResponse>> getAllCoupons(Pageable pageable) {
        Page<CouponResponse> response = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 쿠폰 삭제" , description = "관리자 쿠폰 삭제 API")
    @DeleteMapping("/{coupon-id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable("coupon-id") Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

}
