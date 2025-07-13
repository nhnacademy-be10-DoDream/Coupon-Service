package shop.dodream.couponservice.policy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.service.CouponService;
import shop.dodream.couponservice.policy.dto.CouponPolicyResponse;
import shop.dodream.couponservice.policy.dto.CreateCouponPolicyRequest;
import shop.dodream.couponservice.policy.dto.UpdateCouponPolicyRequest;
import shop.dodream.couponservice.policy.service.PolicyService;

import java.util.List;

@RestController
@RequestMapping("/admin/coupon-policies")
@RequiredArgsConstructor
@Tag(name = "Coupon Policy", description = "쿠폰 정책 관련 API")
public class PolicyController {

    private final PolicyService policyService;
    private final CouponService couponService;

    @Operation(summary = "쿠폰 정책 생성", description = "쿠폰 정책 생성 API")
    @PostMapping
    public ResponseEntity<Void> createPolicy(@RequestBody @Valid CreateCouponPolicyRequest request) {
        policyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "단일 쿠폰 정책 조회", description = "단일 쿠폰 정책 조회 API")
    @GetMapping("/{policy-id}")
    public ResponseEntity<CouponPolicyResponse> getPolicy(@PathVariable("policy-id") Long policyId) {
        CouponPolicyResponse response = policyService.getById(policyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "쿠폰 정책 삭제", description = "쿠폰 정책 삭제 API")
    @DeleteMapping("/{policy-id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable("policy-id") Long policyId) {
        policyService.delete(policyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "쿠폰 정책 수정", description = "쿠폰 정책 수정 API")
    @PutMapping("/{policy-id}")
    public ResponseEntity<Void> updatePolicy(@PathVariable("policy-id") Long policyId, @RequestBody @Valid UpdateCouponPolicyRequest request) {
        policyService.update(policyId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "모든 쿠폰 정책 조회", description = "모든 쿠폰 정책 조회 API")
    @GetMapping
    public ResponseEntity<List<CouponPolicyResponse>> getAllPolicies() {
        return ResponseEntity.status(HttpStatus.OK).body(policyService.getAll());
    }

    @Operation(summary = "특정 쿠폰 정책의 쿠폰 조회", description = "특정 쿠폰 정책의 쿠폰 조회 API")
    @GetMapping("/{policy-id}/coupons")
    public ResponseEntity<List<CouponResponse>> getCouponsByPolicy(@PathVariable("policy-id") Long policyId) {
        List<CouponResponse> response = couponService.getCouponsByPolicy(policyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "정책의 모든 쿠폰 삭제", description = "정책의 모든 쿠폰 논리 삭제 API")
    @DeleteMapping("/{policy-id}/coupons")
    public ResponseEntity<Void> deleteCouponsByPolicy(@PathVariable("policy-id") Long policyId) {
        couponService.deleteCouponsByPolicy(policyId);
        return ResponseEntity.noContent().build();
    }


}
