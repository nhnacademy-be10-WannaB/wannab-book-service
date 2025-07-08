package shop.wannab.book_service.category.service.strategy;

import shop.wannab.book_service.category.entity.Category;

public interface CategoryDeleteStrategy {
    void delete(Category category);
}
