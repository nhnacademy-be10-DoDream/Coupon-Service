package shop.dodream.couponservice.exception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long id) {
        super("coupon not exists: " + id);
    }
}
