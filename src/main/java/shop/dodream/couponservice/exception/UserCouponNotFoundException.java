package shop.dodream.couponservice.exception;

import java.util.List;

public class UserCouponNotFoundException extends RuntimeException {

    public UserCouponNotFoundException(Long id) {
      super("Could not find user coupon with id " + id);
    }

    public UserCouponNotFoundException(List<Long> ids) {
        super("Could not find user coupon with ids " + ids);
    }

    public UserCouponNotFoundException(Long bookId, Long userCouponId) { super("Book Id " + bookId + ": " + userCouponId + " not found"); }
}
