package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderAppliedCouponRequest {

    @NotNull
    private String userId;

    @NotEmpty
    private List<BookPriceRequest> books;
}
