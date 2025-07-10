package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyCouponsRequest {

    @NotEmpty
    @Valid
    private List<BookCouponRequest> requests;
}
