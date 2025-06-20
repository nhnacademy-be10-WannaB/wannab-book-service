package shop.wannab.book_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.entity.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
}
