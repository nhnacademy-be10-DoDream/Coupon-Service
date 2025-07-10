package shop.dodream.couponservice.exception;

public class InvalidUserCouponStatusException extends RuntimeException {
    public InvalidUserCouponStatusException(Long id) {
        super("Invalid user coupon status: " + id);
    }

    public InvalidUserCouponStatusException(String message) {
        super(message);
    }
}
