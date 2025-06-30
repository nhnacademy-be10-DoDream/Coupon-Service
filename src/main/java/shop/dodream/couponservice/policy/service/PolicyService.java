package shop.dodream.couponservice.policy.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.exception.DuplicatePolicyNameException;
import shop.dodream.couponservice.policy.dto.CouponPolicyResponse;
import shop.dodream.couponservice.policy.dto.CreateCouponPolicyRequest;
import shop.dodream.couponservice.policy.dto.UpdateCouponPolicyRequest;
import shop.dodream.couponservice.policy.entity.CouponPolicy;
import shop.dodream.couponservice.policy.repository.CouponPolicyRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 정책 생성
    public void create(CreateCouponPolicyRequest request) {
        if (couponPolicyRepository.existsByName(request.getName())) {
            throw new DuplicatePolicyNameException(request.getName());
        }

        Long discount = request.getDiscountValue();
        switch (request.getDiscountType()) {
            case PERCENTAGE -> {
                if (discount < 0 || discount > 100) {
                    throw new ValidationException("percentage discount must be between 0 and 100");
                }
            }
            case FLAT -> {
                if (discount <= 100) {
                    throw new ValidationException("flat discount must be greater than 100");
                }
            }
        }
        CouponPolicy couponPolicy = request.toEntity();
        couponPolicyRepository.save(couponPolicy);
    }

    //쿠폰 정책 삭제
    public void delete(Long id) {
        if (!couponPolicyRepository.existsById(id)) {
            throw new CouponPolicyNotFoundException(id);
        }
        couponPolicyRepository.deleteById(id);
    }

    // 쿠폰 정책 업데이트
    public void update(Long id, UpdateCouponPolicyRequest request) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(id)
                .orElseThrow(() -> new CouponPolicyNotFoundException(id));
        couponPolicy.update(request);
        couponPolicyRepository.save(couponPolicy);
    }

    // id 로 단일 쿠폰 정책 조회
    @Transactional(readOnly = true)
    public CouponPolicyResponse getById(Long id) {
        CouponPolicy couponPolicy = couponPolicyRepository.findById(id)
                .orElseThrow(() -> new CouponPolicyNotFoundException(id));
        return CouponPolicyResponse.from(couponPolicy);
    }

    // 모든 쿠폰 정책 조회
    @Transactional(readOnly = true)
    public List<CouponPolicyResponse> getAll() {
        return couponPolicyRepository.findAll().stream()
                .map(CouponPolicyResponse::from)
                .toList();
    }

}
