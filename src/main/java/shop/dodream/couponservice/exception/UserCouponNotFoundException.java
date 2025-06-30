package shop.dodream.couponservice.exception;

public class UserCouponNotFoundException extends RuntimeException {
    public UserCouponNotFoundException(Long id) {
      super("Could not find user coupon with id " + id);
    }
}
