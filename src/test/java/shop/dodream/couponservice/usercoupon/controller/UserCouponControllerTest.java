package shop.dodream.couponservice.usercoupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import shop.dodream.couponservice.usercoupon.dto.IssueCouponRequest;
import shop.dodream.couponservice.usercoupon.service.UserCouponService;
import shop.dodream.couponservice.common.resolver.CurrentUserArgumentResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCouponController.class)
@ActiveProfiles("test")
@Import(CurrentUserArgumentResolver.class)
class UserCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserCouponService userCouponService;

    @Test
    @DisplayName("POST /admin/user-coupons")
    void issueCoupon() throws Exception {
        IssueCouponRequest req = new IssueCouponRequest("u", 1L);
        mockMvc.perform(post("/admin/user-coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
        verify(userCouponService).issuedCoupon(any());
    }

    @Test
    @DisplayName("PUT /coupons/me/apply-multiple")
    void applyCoupons() throws Exception {
        String body = "{\"requests\":[{\"bookId\":1,\"userCouponId\":2}]}";
        mockMvc.perform(put("/coupons/me/apply-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-USER-ID", "u")
                        .content(body))
                .andExpect(status().isNoContent());
        verify(userCouponService).applyCoupons(eq("u"), any());
    }

    @Test
    @DisplayName("PUT /admin/user-coupons/{user-id}/revoke/{user-coupon-id} → 204, 서비스 호출")
    void revokeCoupon() throws Exception {
        String userId = "u";
        Long couponId = 5L;

        mockMvc.perform(put("/admin/user-coupons/{user-id}/revoke/{user-coupon-id}", userId, couponId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userCouponService).revokeCoupon(userId, couponId);
    }

    @Test
    @DisplayName("PUT /coupons/me/use-multiple")
    void useCoupons() throws Exception {
        String body = "{\"userCouponIds\":[1,2]}";
        mockMvc.perform(put("/coupons/me/use-multiple")
                        .header("X-USER-ID", "u")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
        verify(userCouponService).useCoupons(eq("u"), any());
    }

    @Test
    @DisplayName("GET /coupons/me/available")
    void getAvailableCoupons() throws Exception {
        given(userCouponService.getAvailableCoupons("u")).willReturn(List.of());
        mockMvc.perform(get("/coupons/me/available")
                        .header("X-USER-ID", "u"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /admin/user-coupons/issue_multiple")
    void issueCouponsToUsers() throws Exception {
        String body = "{\"couponId\":1,\"userIds\":[\"u1\",\"u2\"]}";
        mockMvc.perform(post("/admin/user-coupons/issue_multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        verify(userCouponService).issueCouponsToUsers(any());
    }

    @Test
    @DisplayName("GET /coupons/book/{id}")
    void getBookAvailableCoupons() throws Exception {
        given(userCouponService.getBookAvailableCoupons("u", 1L)).willReturn(List.of());
        mockMvc.perform(get("/coupons/book/1")
                        .header("X-USER-ID", "u"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /coupons/me/order-valid")
    void orderValid() throws Exception {
        String body = "{\"userId\":\"u\",\"books\":[{\"bookId\":1,\"bookPrice\":1000,\"couponId\":1}]}";
        mockMvc.perform(post("/coupons/me/order-valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
        verify(userCouponService).getOrderAppliedCoupons(any());
    }

    @Test
    @DisplayName("GET /admin/user-coupons/{coupon-id}")
    void getUserCouponsByCoupon() throws Exception {
        given(userCouponService.getUserCouponsByCoupon(1L)).willReturn(List.of());
        mockMvc.perform(get("/admin/user-coupons/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /admin/user-coupons/{coupon-id}")
    void deleteUserCouponsByCoupon() throws Exception {
        mockMvc.perform(delete("/admin/user-coupons/1"))
                .andExpect(status().isNoContent());
        verify(userCouponService).deleteUserCouponsByCoupon(1L);
    }

    @Test
    @DisplayName("PUT /admin/user-coupons/revokes/used")
    void revokeUsedCoupons() throws Exception {
        mockMvc.perform(put("/admin/user-coupons/revokes/used")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "u")
                        .content("[1,2]"))
                .andExpect(status().isNoContent());
        verify(userCouponService).revokeUsedCoupons(eq("u"), any());
    }
}