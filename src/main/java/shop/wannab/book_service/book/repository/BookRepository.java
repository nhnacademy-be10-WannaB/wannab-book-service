package shop.wannab.book_service.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.dto.BookInfoForOrderProjection;

import java.util.List;
public interface BookRepository extends JpaRepository<Book, Long>, BookRedisRepository {
    boolean existsByBookIdAndIsOnSaleTrue(long bookId);

    List<BookInfoForOrderProjection> findByBookIdIn(List<Long> ids);

}