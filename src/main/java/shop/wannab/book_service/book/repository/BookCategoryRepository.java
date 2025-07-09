package shop.wannab.book_service.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.wannab.book_service.book.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    @Query("SELECT bc FROM book_category bc JOIN FETCH bc.category WHERE bc.book.bookId = :bookId")
    List<BookCategory> findCategoriesByBookIdWithFetchJoin(@Param("bookId") Long bookId);

    @Query("SELECT DISTINCT bc.category.id FROM book_category bc WHERE bc.book.bookId IN :bookIds")
    List<Long> findCategoryIdsByBookIds(@Param("bookIds") List<Long> bookIds);
}
