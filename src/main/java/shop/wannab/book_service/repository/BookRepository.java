package shop.wannab.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.entity.Book;
import shop.wannab.book_service.entity.dto.BookInfoForOrderProjection;

import java.util.List;
public interface BookRepository extends JpaRepository<Book, Long>, BookRedisRepository {
    boolean existsByBookIdAndIsOnSaleTrue(long bookId);

    List<BookInfoForOrderProjection> findByIdIn(List<Long> ids);

}