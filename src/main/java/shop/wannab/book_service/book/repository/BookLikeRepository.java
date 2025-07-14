package shop.wannab.book_service.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.wannab.book_service.book.entity.Book;
import shop.wannab.book_service.book.entity.BookLike;

import java.util.List;

public interface BookLikeRepository extends JpaRepository<BookLike,Long> {
    // 도서 좋아요 여부 조회
     boolean existsByUserIdAndBook_BookId(Long userId, Long bookId);

    // 도서 좋아요 취소
     void deleteByUserIdAndBook_BookId(Long userId, Long bookId);

    @Query("SELECT bl.book FROM book_like bl WHERE bl.userId = :userId")
    Page<Book> findBooksLikedByUserId(@Param("userId") Long userId, Pageable pageable);


    @Query("""
    SELECT bl.book
    FROM book_like bl
    GROUP BY bl.book
    ORDER BY COUNT(bl) DESC
    """)
    List<Book> findTop10BooksByLikeCount(Pageable pageable);
}
