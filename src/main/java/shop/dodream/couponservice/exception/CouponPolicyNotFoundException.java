package shop.dodream.couponservice.exception;

public class CouponPolicyNotFoundException extends RuntimeException {
    public CouponPolicyNotFoundException(Long id) {
        super(String.format("Coupon policy not found: %s", id));
    }

    public CouponPolicyNotFoundException(String message) {
        super(message);
    }
}
