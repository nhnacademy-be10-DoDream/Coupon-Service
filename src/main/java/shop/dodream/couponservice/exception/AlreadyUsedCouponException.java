package shop.dodream.couponservice.exception;

public class AlreadyUsedCouponException extends RuntimeException {
    public AlreadyUsedCouponException(Long id) {
        super("already used coupon: " + id);
    }
}
