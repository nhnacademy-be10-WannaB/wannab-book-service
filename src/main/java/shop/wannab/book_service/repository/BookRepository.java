package shop.wannab.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>, BookRedisRepository {
    boolean existsByBookIdAndIsOnSaleTrue(long bookId);

}