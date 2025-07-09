package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookCouponRequest {

    @NotNull
    private Long bookId;

    private Long userCouponId;
}
