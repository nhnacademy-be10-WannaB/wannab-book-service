package shop.wannab.book_service.category.repository;

import shop.wannab.book_service.category.entity.Category;

public interface DeleteCategoryStrategy {
    void delete(Category category);
}
