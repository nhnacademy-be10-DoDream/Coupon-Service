package shop.dodream.couponservice.usercoupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
    private Long categoryId;
    private String categoryName;
    private Long depth;
    private Long parentId;
    private List<CategoryTreeResponse> children = new ArrayList<>();
}

