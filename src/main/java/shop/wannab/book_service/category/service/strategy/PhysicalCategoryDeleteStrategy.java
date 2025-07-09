package shop.wannab.book_service.category.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.repository.CategoryRepository;

/**
 * 해당 카테고리 데이터를 물리적으로 삭제하는 전략
 * 추후 논리적 삭제로 변경 예정
 */
@Component
@RequiredArgsConstructor
public class PhysicalCategoryDeleteStrategy implements CategoryDeleteStrategy {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void delete(Category targetCategory) {
        categoryRepository.deleteCategoryWithBookCategories(targetCategory);
    }
}

