package shop.wannab.book_service.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreateRequest {
    private Long parentId;
    private String name;
}
