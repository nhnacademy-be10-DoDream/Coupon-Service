package shop.dodream.couponservice.usercoupon.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shop.dodream.couponservice.usercoupon.dto.BookDetailResponse;
import shop.dodream.couponservice.usercoupon.dto.CategoryTreeResponse;
import java.util.List;

@FeignClient(name = "book")
public interface BookServiceClient {
    @GetMapping("/public/books/{book-id}/categories")
    List<CategoryTreeResponse> getCategoriesByBookId(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/books/{book-id}")
    BookDetailResponse getBookSalePrice(@PathVariable("book-id") Long bookId);
}
