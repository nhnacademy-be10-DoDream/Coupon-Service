package shop.dodream.couponservice.usercoupon.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.couponservice.common.Grade;

import java.util.List;

@FeignClient("user")
public interface UserServiceClient {

    @GetMapping("/users")
    List<String> getUsers(@RequestParam(value = "gradeType") Grade grade,
                          @RequestParam(value = "birth-month") Integer birthMonth);
}
