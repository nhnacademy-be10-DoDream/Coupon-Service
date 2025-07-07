package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IssueCouponToUsersRequest {

    @NotNull
    private Long couponId;

    private List<String> userIds;

    private UserSearchCondition condition;

}
