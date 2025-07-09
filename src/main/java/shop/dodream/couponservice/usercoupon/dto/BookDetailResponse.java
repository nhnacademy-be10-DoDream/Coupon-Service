package shop.dodream.couponservice.usercoupon.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class BookDetailResponse {
    private Long bookId;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String isbn;
    private LocalDate publishedAt;
    private Long salePrice;
    private Long regularPrice;
    private Boolean isGiftable;
    private List<String> bookUrls;
    private Long discountRate;
}