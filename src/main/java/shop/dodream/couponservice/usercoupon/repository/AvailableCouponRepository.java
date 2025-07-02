package shop.dodream.couponservice.usercoupon.repository;

import shop.dodream.couponservice.usercoupon.dto.AvailableCouponResponse;
import shop.dodream.couponservice.usercoupon.dto.BookAvailableCouponResponse;

import java.util.List;

public interface AvailableCouponRepository {
    List<AvailableCouponResponse> findAllAvailableByUserId(String userId);
    List<BookAvailableCouponResponse> findAvailableCouponsForBook(String userId,
                                                                  Long bookId,
                                                                  List<Long> categoryIds,
                                                                  Long bookPrice);
}
