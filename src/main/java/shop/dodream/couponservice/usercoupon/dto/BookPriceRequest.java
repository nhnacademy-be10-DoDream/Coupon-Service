package shop.dodream.couponservice.usercoupon.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookPriceRequest {

    @NotNull
    private Long bookId;

    @NotNull
    private Long bookPrice;

    @NotNull
    private Long couponId;
}
