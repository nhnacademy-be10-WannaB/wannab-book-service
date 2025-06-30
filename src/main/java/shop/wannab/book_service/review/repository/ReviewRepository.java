package shop.wannab.book_service.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.wannab.book_service.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Page<Review> findByBook_BookId(Long bookId, Pageable pageable);

    Page<Review> findByUserId (Long userId, Pageable pageable);

    @Query("SELECT AVG(r.reviewScore) FROM reviews r WHERE r.book.bookId = :bookId")
    Double findAverageScoreByBookId(@Param("bookId") Long bookId);

    boolean existsByBook_BookIdAndUserId(Long bookId, Long userId);

}
