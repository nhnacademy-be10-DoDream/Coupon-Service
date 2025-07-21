package shop.dodream.couponservice.usercoupon.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class UseCouponsRequest {
    private List<Long> userCouponIds;
}
