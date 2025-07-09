package shop.wannab.book_service.category.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryIdsResponse {
    private List<Long> categoryIds;
}
