package shop.dodream.couponservice.policy.entity;

import jakarta.persistence.*;
import shop.dodream.couponservice.common.DiscountType;

import java.time.ZonedDateTime;

@Entity
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false)
    private Long discountValue;

    private Long minPurchaseAmount;

    private Long maxDiscountAmount;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column(nullable = false)
    private ZonedDateTime endDate;
}
