package shop.dodream.couponservice.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import shop.dodream.couponservice.coupon.dto.CouponResponse;
import shop.dodream.couponservice.coupon.dto.CreateCouponRequest;
import shop.dodream.couponservice.coupon.service.CouponService;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
@ActiveProfiles("test")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    @Test
    @DisplayName("POST /admin/coupons 는 201을 반환한다")
    void createCoupon() throws Exception {
        CreateCouponRequest req = new CreateCouponRequest(1L, 2L, null);
        mockMvc.perform(post("/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
        verify(couponService).createCoupon(any());
    }

    @Test
    @DisplayName("GET /admin/coupons/{id} 는 쿠폰 정보를 반환한다")
    void getCoupon() throws Exception {
        CouponResponse res = CouponResponse.builder()
                .couponId(1L)
                .policyId(1L)
                .policyName("policy")
                .build();
        given(couponService.getCoupon(1L)).willReturn(res);

        mockMvc.perform(get("/admin/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").value(1L))
                .andExpect(jsonPath("$.policyName").value("policy"));
    }

    @Test
    @DisplayName("GET /admin/coupons 는 페이지 결과를 반환한다")
    void getAllCoupons() throws Exception {
        Page<CouponResponse> page = new PageImpl<>(List.of());
        given(couponService.getAllCoupons(any(PageRequest.class))).willReturn(page);

        mockMvc.perform(get("/admin/coupons"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /admin/coupons/{id}")
    void deleteCoupon() throws Exception {
        mockMvc.perform(delete("/admin/coupons/1"))
                .andExpect(status().isNoContent());
        verify(couponService).deleteCoupon(1L);
    }
}