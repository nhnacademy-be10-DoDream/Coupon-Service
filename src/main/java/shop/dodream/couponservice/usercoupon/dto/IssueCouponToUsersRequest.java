package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class IssueCouponToUsersRequest {

    @NotNull
    private Long couponId;

    @NotEmpty
    private List<String> userIds;
}
