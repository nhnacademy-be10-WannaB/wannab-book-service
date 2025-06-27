package shop.wannab.book_service.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.book.entity.BookLike;

public interface BookLikeRepository extends JpaRepository<BookLike,Long> {
    // 도서 좋아요 여부 조회
    // boolean existsByUserIdAndBook_BookId(Long userId, Long bookId);

    // 도서 좋아요 취소
    // void deleteByUserIdAndBook_BookId(Long userId, Long bookId);

    // 도서 좋아요 기본제공
}
