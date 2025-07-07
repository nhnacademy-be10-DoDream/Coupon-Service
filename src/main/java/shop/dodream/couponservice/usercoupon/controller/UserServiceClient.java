package shop.dodream.couponservice.usercoupon.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.dodream.couponservice.usercoupon.dto.UserSearchCondition;

import java.util.List;

@FeignClient("user")
public interface UserServiceClient {

    @GetMapping("/users")
    List<String> getUsers(@RequestBody UserSearchCondition condition);
}
