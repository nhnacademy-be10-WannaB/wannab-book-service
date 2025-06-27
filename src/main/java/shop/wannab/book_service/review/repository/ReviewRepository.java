package shop.wannab.book_service.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.wannab.book_service.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review,Long> {

}
