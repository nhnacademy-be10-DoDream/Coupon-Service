package shop.dodream.couponservice.usercoupon.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UseCouponsRequest {
    private List<Long> userCouponIds;
}
