package shop.dodream.couponservice.usercoupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.common.CouponStatus;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.exception.AlreadyUsedCouponException;
import shop.dodream.couponservice.exception.InvalidCouponPolicyException;

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
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    private ZonedDateTime issuedAt;

    private ZonedDateTime usedAt;

    @Column(nullable = false)
    private ZonedDateTime expiredAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    public void use() {
        if (this.usedAt != null) {
            throw new AlreadyUsedCouponException(this.userCouponId);
        }
        if (this.expiredAt.isBefore(ZonedDateTime.now())) {
            this.status = CouponStatus.EXPIRED;
            throw new AlreadyUsedCouponException(this.userCouponId);
        }
        this.usedAt = ZonedDateTime.now();
        this.status = CouponStatus.USED;
    }

    public void apply() {
        if (this.status != CouponStatus.AVAILABLE) {
            throw new InvalidCouponPolicyException("쿠폰을 적용할 수 없는 상태입니다.");
        }
        this.status = CouponStatus.APPLIED;
    }

    public void revoke() {
        if (this.status == CouponStatus.AVAILABLE) {
            throw new InvalidCouponPolicyException("이미 사용 가능한 쿠폰입니다.");
        }
        this.status = CouponStatus.AVAILABLE;
    }

}
