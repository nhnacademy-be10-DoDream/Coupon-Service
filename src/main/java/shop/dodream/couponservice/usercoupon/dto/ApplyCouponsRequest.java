package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplyCouponsRequest {

    @NotEmpty
    @Valid
    private List<BookCouponRequest> requests;
}
