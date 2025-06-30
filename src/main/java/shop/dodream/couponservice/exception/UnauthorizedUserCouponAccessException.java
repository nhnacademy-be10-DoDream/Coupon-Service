package shop.dodream.couponservice.exception;

public class UnauthorizedUserCouponAccessException extends RuntimeException {
    public UnauthorizedUserCouponAccessException(String userId, Long couponId) {

        super(String.format("User %s has no access to coupon %s", userId, couponId));
    }
}
