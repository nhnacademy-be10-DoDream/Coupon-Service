package shop.dodream.couponservice.policy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shop.dodream.couponservice.common.DiscountType;
import shop.dodream.couponservice.common.ExpiredStrategy;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.service.CouponService;
import shop.dodream.couponservice.policy.dto.CouponPolicyResponse;
import shop.dodream.couponservice.policy.dto.CreateCouponPolicyRequest;
import shop.dodream.couponservice.policy.dto.UpdateCouponPolicyRequest;
import shop.dodream.couponservice.policy.service.PolicyService;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
@ActiveProfiles("test")
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PolicyService policyService;
    @MockBean
    private CouponService couponService;

    @Test
    @DisplayName("POST /admin/coupon-policies 생성")
    void createPolicy() throws Exception {
        CreateCouponPolicyRequest req = new CreateCouponPolicyRequest(
                "name", DiscountType.FLAT, 1000L, 0L, 2000L, ExpiredStrategy.FIXED, ZonedDateTime.now(), null);
        mockMvc.perform(post("/admin/coupon-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
        verify(policyService).create(any());
    }

    @Test
    @DisplayName("GET /admin/coupon-policies/{id}")
    void getPolicy() throws Exception {
        CouponPolicyResponse res = CouponPolicyResponse.builder()
                .id(1L).name("n").build();
        given(policyService.getById(1L)).willReturn(res);
        mockMvc.perform(get("/admin/coupon-policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /admin/coupon-policies")
    void getAll() throws Exception {
        given(policyService.getAll()).willReturn(List.of());
        mockMvc.perform(get("/admin/coupon-policies"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /admin/coupon-policies/{id}/coupons")
    void getCouponsByPolicy() throws Exception {
        given(couponService.getCouponsByPolicy(1L)).willReturn(List.of());
        mockMvc.perform(get("/admin/coupon-policies/1/coupons"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /admin/coupon-policies/{id}/coupons")
    void deleteCouponsByPolicy() throws Exception {
        mockMvc.perform(delete("/admin/coupon-policies/1/coupons"))
                .andExpect(status().isNoContent());
        verify(couponService).deleteCouponsByPolicy(1L);
    }

    @Test
    @DisplayName("DELETE /admin/coupon-policies/{id}")
    void deletePolicy() throws Exception {
        mockMvc.perform(delete("/admin/coupon-policies/1"))
                .andExpect(status().isNoContent());
        verify(policyService).delete(1L);
    }

    @Test
    @DisplayName("PUT /admin/coupon-policies/{id}")
    void updatePolicy() throws Exception {
        UpdateCouponPolicyRequest req = new UpdateCouponPolicyRequest(
                "n", DiscountType.FLAT, 1000L, 0L, 2000L, ExpiredStrategy.PLUS, null, 30L);
        mockMvc.perform(put("/admin/coupon-policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
        verify(policyService).update(eq(1L), any());
    }
}