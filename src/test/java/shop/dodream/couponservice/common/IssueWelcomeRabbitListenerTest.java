package shop.dodream.couponservice.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.dodream.couponservice.common.rabbitmq.IssueWelcomeRabbitListener;
import shop.dodream.couponservice.coupon.entity.Coupon;
import shop.dodream.couponservice.coupon.repository.CouponRepository;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.exception.UserNotFoundException;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
import shop.dodream.couponservice.usercoupon.service.UserCouponService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class IssueWelcomeRabbitListenerTest {

    @Mock  private UserCouponService       userCouponService;
    @Mock  private CouponRepository        couponRepository;
    @Mock  private CouponPolicyRepository  couponPolicyRepository;

    @InjectMocks
    private IssueWelcomeRabbitListener listener;

    @Test
    @DisplayName("issueWelcome : 정책/쿠폰 존재 시 issuedCoupon 요청을 발행한다")
    void issueWelcome_success() {
        CouponPolicy policy = CouponPolicy.builder()
                .policyId(1L)
                .name("웰컴")
                .build();
        Coupon coupon = Coupon.builder()
                .couponId(2L)
                .couponPolicy(policy)
                .build();

        given(couponPolicyRepository.findByNameContainsAndDeletedFalse("웰컴"))
                .willReturn(Optional.of(policy));
        given(couponRepository.findByCouponPolicyPolicyIdAndDeletedFalse(1L))
                .willReturn(List.of(coupon));

        listener.issueWelcome("u1");

        ArgumentCaptor<IssueCouponRequest> captor =
                ArgumentCaptor.forClass(IssueCouponRequest.class);

        verify(userCouponService).issuedCoupon(captor.capture());

        IssueCouponRequest req = captor.getValue();
        assertThat(req.getUserId()).isEqualTo("u1");
        assertThat(req.getCouponId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("issueWelcome : userId 가 null 이면 UserNotFoundException")
    void issueWelcome_nullUser() {
        assertThatThrownBy(() -> listener.issueWelcome(null))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("issueWelcome : 정책이 없으면 CouponPolicyNotFoundException")
    void issueWelcome_noPolicy() {
        given(couponPolicyRepository.findByNameContainsAndDeletedFalse("웰컴"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> listener.issueWelcome("u"))
                .isInstanceOf(CouponPolicyNotFoundException.class);
    }
}
