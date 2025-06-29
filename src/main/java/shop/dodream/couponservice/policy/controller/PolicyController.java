package shop.dodream.couponservice.policy.controller;

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
@RequestMapping("/admin/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<Void> createPolicy(@RequestBody @Valid CreateCouponPolicyRequest request) {
        policyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<CouponPolicyResponse> getPolicy(@PathVariable Long policyId) {
        CouponPolicyResponse response = policyService.getById(policyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long policyId) {
        policyService.delete(policyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{policyId}")
    public ResponseEntity<Void> updatePolicy(@PathVariable Long policyId, @RequestBody @Valid UpdateCouponPolicyRequest request) {
        policyService.update(policyId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<CouponPolicyResponse>> getAllPolicies() {
        return ResponseEntity.status(HttpStatus.OK).body(policyService.getAll());
    }

    @GetMapping("/{policyId}/coupons")
    public ResponseEntity<List<CouponResponse>> getCouponsByPolicy(@PathVariable Long policyId) {
        List<CouponResponse> response = couponService.getCouponsByPolicy(policyId);
        return ResponseEntity.ok(response);
    }


}
