package shop.dodream.couponservice.coupon.entity;

import jakarta.persistence.*;
import shop.dodream.couponservice.policy.entity.CouponPolicy;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private CouponPolicy couponPolicy;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private Long categoryId;

}
