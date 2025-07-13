package shop.dodream.couponservice.usercoupon.dto;

import lombok.Getter;
import shop.dodream.couponservice.common.Grade;

@Getter
public class UserSearchCondition {
    private Grade grade;
    private Integer birthMonth;
}
