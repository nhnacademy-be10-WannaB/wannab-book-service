package shop.wannab.book_service.category.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.wannab.book_service.category.dto.response.CategoryResponse;
import shop.wannab.book_service.category.entity.Category;

public interface CategoryQueryRepository {
    List<CategoryResponse> findParentCategories();
    Page<CategoryResponse> findParentCategories(Pageable pageable);
    Page<CategoryResponse> findChildCategoriesByParentId(Long parentId, Pageable pageable);
    void deleteCategoryWithBookCategories(Category targetCategory);
    List<Category> findRootsWithChildren();
}
