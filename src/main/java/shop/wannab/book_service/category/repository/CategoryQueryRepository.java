package shop.wannab.book_service.category.repository;

import java.util.List;
import shop.wannab.book_service.category.dto.response.CategoryResponse;

public interface CategoryQueryRepository {
    List<CategoryResponse> findParentCategories();
    List<CategoryResponse> findChildCategoriesByParentId(Long parentId);
}
