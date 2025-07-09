package shop.wannab.book_service.category.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.repository.CategoryRepository;

/**
 * 해당 카테고리 데이터를 논리적으로 삭제하는 전략
 */
@Component
@RequiredArgsConstructor
public class SoftCategoryDeleteStrategy implements CategoryDeleteStrategy {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void delete(Category category) {
        category.delete();
        categoryRepository.save(category);
    }
}

