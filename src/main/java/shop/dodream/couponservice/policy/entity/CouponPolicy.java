package shop.dodream.couponservice.policy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.policy.dto.UpdateCouponPolicyRequest;

import java.time.ZonedDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false)
    @Min(0)
    private Long discountValue;

    @Min(0)
    private Long minPurchaseAmount;

    @Min(0)
    private Long maxDiscountAmount;

    @Column(nullable = false)
    private ZonedDateTime startDate;

    @Column(nullable = false)
    private ZonedDateTime endDate;

    public void update(UpdateCouponPolicyRequest request) {
        this.name = request.getName();
        this.discountType = request.getDiscountType();
        this.discountValue = request.getDiscountValue();
        this.minPurchaseAmount = request.getMinPurchaseAmount();
        this.maxDiscountAmount = request.getMaxDiscountAmount();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
    }
}
