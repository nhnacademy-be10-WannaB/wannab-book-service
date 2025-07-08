package shop.wannab.book_service.category.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryQueryRepository {
    List<Category> findByParentIsNull();
    Optional<Category> findByName(String categoryName);
}
