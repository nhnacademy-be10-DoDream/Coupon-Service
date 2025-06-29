package shop.dodream.couponservice.usercoupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.exception.AlreadyUsedCouponException;

import java.time.ZonedDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    private ZonedDateTime issuedAt;

    private ZonedDateTime usedAt;

    @Column(nullable = false)
    private ZonedDateTime expiredAt;

    public void use() {
        if (this.usedAt != null) {
            throw new AlreadyUsedCouponException(this.userCouponId);
        }
        this.usedAt = ZonedDateTime.now();
    }
}
