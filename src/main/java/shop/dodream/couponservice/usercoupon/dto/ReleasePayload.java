package shop.dodream.couponservice.usercoupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReleasePayload {
    private String userId;
    private List<Long> userCouponIds;
}
