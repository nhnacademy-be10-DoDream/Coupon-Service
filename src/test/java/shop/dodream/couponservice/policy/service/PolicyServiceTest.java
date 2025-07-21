package shop.dodream.couponservice.policy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.exception.DuplicatePolicyNameException;
import shop.dodream.couponservice.policy.dto.CreateCouponPolicyRequest;
import shop.dodream.couponservice.policy.dto.UpdateCouponPolicyRequest;
import shop.dodream.couponservice.policy.dto.CouponPolicyResponse;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import jakarta.validation.ValidationException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PolicyServiceTest {

    @Mock
    private CouponPolicyRepository repository;

    @InjectMocks
    private PolicyService policyService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateCouponPolicyRequest createReq() {
        return new CreateCouponPolicyRequest(
                "name",
                DiscountType.FLAT,
                1000L,
                0L,
                2000L,
                ExpiredStrategy.FIXED,
                ZonedDateTime.now().plusDays(1),
                null
        );
    }

    private CouponPolicy policy() {
        return createReq().toEntity();
    }

    @Test
    @DisplayName("정책 생성 성공")
    void create() {
        given(repository.existsByName("name")).willReturn(false);
        policyService.create(createReq());
        verify(repository).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("중복 이름은 예외")
    void duplicateName() {
        given(repository.existsByName("name")).willReturn(true);
        assertThatThrownBy(() -> policyService.create(createReq()))
                .isInstanceOf(DuplicatePolicyNameException.class);
    }

    @Test
    @DisplayName("삭제 처리")
    void delete() {
        CouponPolicy p = policy();
        given(repository.findByPolicyIdAndDeletedFalse(1L)).willReturn(Optional.of(p));
        policyService.delete(1L);
        ArgumentCaptor<CouponPolicy> captor = ArgumentCaptor.forClass(CouponPolicy.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("정책 업데이트")
    void update() {
        CouponPolicy p = policy();
        given(repository.findByPolicyIdAndDeletedFalse(1L)).willReturn(Optional.of(p));
        UpdateCouponPolicyRequest req = new UpdateCouponPolicyRequest(
                "new",
                DiscountType.FLAT,
                2000L,
                100L,
                3000L,
                ExpiredStrategy.FIXED,
                ZonedDateTime.now().plusDays(2),
                null
        );
        policyService.update(1L, req);
        verify(repository).save(any(CouponPolicy.class));
        assertThat(p.getName()).isEqualTo("new");
        assertThat(p.getDiscountValue()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("정책 조회 실패")
    void getByIdFail() {
        given(repository.findByPolicyIdAndDeletedFalse(1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> policyService.getById(1L))
                .isInstanceOf(CouponPolicyNotFoundException.class);
    }

    @Test
    @DisplayName("모든 정책 조회")
    void getAll() {
        given(repository.findAllByDeletedFalse()).willReturn(List.of(policy()));
        assertThat(policyService.getAll()).hasSize(1);
    }

    @Test
    @DisplayName("정책 조회 성공")
    void getById() {
        CouponPolicy p = policy();
        given(repository.findByPolicyIdAndDeletedFalse(1L)).willReturn(Optional.of(p));
        CouponPolicyResponse res = policyService.getById(1L);
        assertThat(res.getId()).isEqualTo(p.getPolicyId());
    }

    @Test
    @DisplayName("할인값 검증 실패 - 퍼센트 100 초과")
    void invalidPercentageValue() {
        CreateCouponPolicyRequest req = new CreateCouponPolicyRequest(
                "name",
                DiscountType.PERCENTAGE,
                110L,
                0L,
                2000L,
                ExpiredStrategy.FIXED,
                ZonedDateTime.now().plusDays(1),
                null
        );
        given(repository.existsByName("name")).willReturn(false);
        assertThatThrownBy(() -> policyService.create(req))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("할인값 검증 실패 - 고정값 100 이하")
    void invalidFlatValue() {
        CreateCouponPolicyRequest req = new CreateCouponPolicyRequest(
                "name",
                DiscountType.FLAT,
                50L,
                0L,
                2000L,
                ExpiredStrategy.FIXED,
                ZonedDateTime.now().plusDays(1),
                null
        );
        given(repository.existsByName("name")).willReturn(false);
        assertThatThrownBy(() -> policyService.create(req))
                .isInstanceOf(ValidationException.class);
    }
}