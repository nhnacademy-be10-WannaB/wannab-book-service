package shop.wannab.book_service.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.book.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

}
