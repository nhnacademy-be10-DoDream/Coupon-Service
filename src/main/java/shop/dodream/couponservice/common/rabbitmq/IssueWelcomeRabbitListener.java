package shop.dodream.couponservice.common.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.exception.UserNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
import shop.dodream.couponservice.usercoupon.service.UserCouponService;

@RequiredArgsConstructor
@Component
public class IssueWelcomeRabbitListener {

    private final UserCouponService userCouponService;
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    @RabbitListener(queues = "${coupon.rabbit.queue}", containerFactory = "rabbitListenerContainerFactory")
    public void issueWelcome(String userId) {
        if (userId == null) {
            throw new UserNotFoundException("User not found");
        }
        CouponPolicy welcomePolicy = couponPolicyRepository.findByNameContainsAndDeletedFalse("웰컴")
                .orElseThrow(() -> new CouponPolicyNotFoundException("웰컴 쿠폰 정책이 없습니다."));

        Long welcomePolicyId = welcomePolicy.getPolicyId();

        Long welcomeCouponId = couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(welcomePolicyId).getFirst().getCouponId();

        if (welcomeCouponId == null) {
            throw new CouponNotFoundException("웰컴 쿠폰이 존재하지 않습니다.");
        }

        IssueCouponRequest issueCouponRequest = new IssueCouponRequest(userId, welcomeCouponId);

        userCouponService.issuedCoupon(issueCouponRequest);
    }


}
