package shop.dodream.couponservice.usercoupon.entity;

import jakarta.persistence.*;
import shop.dodream.couponservice.coupon.entity.Coupon;

import java.time.ZonedDateTime;

@Entity
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
}
