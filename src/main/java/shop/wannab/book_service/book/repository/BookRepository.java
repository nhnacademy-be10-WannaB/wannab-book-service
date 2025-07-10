package shop.wannab.book_service.book.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.wannab.book_service.book.dto.BookIdTitlePriceProjection;
import shop.wannab.book_service.book.dto.BookInfoForOrderProjection;
import shop.wannab.book_service.book.entity.Book;
public interface BookRepository extends JpaRepository<Book, Long>, BookRedisRepository {

    boolean existsByBookIdAndStatusTrue(long bookId);

    @Query("SELECT b.bookId FROM books b WHERE b.bookId IN :bookIds")
    List<Long> findBookIdByBookIdIn(List<Long> bookIds);


    List<BookInfoForOrderProjection> findByBookIdIn(List<Long> ids);

    List<BookIdTitlePriceProjection> queryByBookIdIn(List<Long> ids);

    //도서 상세 조회
    @EntityGraph(attributePaths = {
            "bookAuthors.author"
    })
    @Query("SELECT b FROM books b WHERE b.bookId = :bookId")
    Book findBookDetail(@Param("bookId") Long bookId);

    // 도서 목록 검색
    @EntityGraph(attributePaths = {
            "bookAuthors.author"
    })
    Page<Book> findAll(Pageable pageable);

    boolean existsByIsbn(String isbn);

    @Query(value = "SELECT b FROM books b JOIN b.bookCategories bc WHERE bc.category.id = :categoryId",
           countQuery = "SELECT count(b) FROM books b JOIN b.bookCategories bc WHERE bc.category.id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

}