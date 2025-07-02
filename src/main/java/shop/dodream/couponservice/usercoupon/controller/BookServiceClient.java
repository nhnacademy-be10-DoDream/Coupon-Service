package shop.dodream.couponservice.usercoupon.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shop.dodream.couponservice.usercoupon.dto.CategoryTreeResponse;
import java.util.List;

@FeignClient(name = "book")
public interface BookServiceClient {
    @GetMapping("/books/{book-id}/categories")
    List<CategoryTreeResponse> getCategoriesByBookId(@PathVariable("book-id") Long bookId);
}
