package shop.dodream.couponservice.usercoupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UseCouponsRequest {
    private List<Long> userCouponIds;
}
