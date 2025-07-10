package shop.dodream.couponservice.exception;

public class DuplicateUserCouponAppliedException extends RuntimeException {
    public DuplicateUserCouponAppliedException(String message) {
        super(message);
    }
}
